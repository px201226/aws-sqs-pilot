package com.example.awssqspilot.config;

import java.util.regex.Pattern;
import net.sf.log4jdbc.Properties;
import net.sf.log4jdbc.log.SpyLogDelegator;
import net.sf.log4jdbc.sql.Spy;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollector;
import net.sf.log4jdbc.sql.resultsetcollector.ResultSetCollectorPrinter;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jSpyLogDelegator implements SpyLogDelegator {

	/** SQL호출, 소요시간 logger */
	private final Logger sqlTimingLogger = LoggerFactory.getLogger("jdbc.sqltiming");

	/** SQL호출결과 TABLE출력 */
	private final Logger resultSetTableLogger = LoggerFactory.getLogger("jdbc.resultsettable");

	/** 개행 */
	private static String nl = System.getProperty("line.separator");

	@Override
	public boolean isJdbcLoggingEnabled() {
		return sqlTimingLogger.isErrorEnabled();
	}

	@Override
	public void exceptionOccured(Spy spy, String methodCall, Exception e, String sql, long execTime) {
		String classType = spy.getClassType();
		Integer spyNo = spy.getConnectionNumber();
		String header = spyNo + ". " + classType + "." + methodCall;
		if (sql == null) {
			sqlTimingLogger.error(header, e);
		} else {
			sql = formatSql(sql);
			if (sqlTimingLogger.isDebugEnabled()) {
				sqlTimingLogger.error(getDebugInfo() + nl + spyNo + ". " + sql + " {FAILED after " + execTime + " msec}", e);
			} else {
				sqlTimingLogger.error(header + " FAILED! " + sql + " {FAILED after " + execTime + " msec}", e);
			}
		}
	}

	@Override
	public void resultSetCollected(ResultSetCollector resultSetCollector) {
		if (!resultSetTableLogger.isDebugEnabled()) {
			return;
		}
		String resultsToPrint = new ResultSetCollectorPrinter().getResultSetToPrint(resultSetCollector);
		resultSetTableLogger.debug(resultsToPrint);
	}

	@Override
	public void sqlTimingOccurred(Spy spy, long execTime, String methodCall, String sql) {

		if (sqlTimingLogger.isErrorEnabled() && (!Properties.isDumpSqlFilteringOn() || shouldSqlBeLogged(sql))) {

			if (Properties.isSqlTimingErrorThresholdEnabled() && execTime >= Properties.getSqlTimingErrorThresholdMsec()) {
				sqlTimingLogger.error(buildSqlTimingDump(spy, execTime, methodCall, sql, sqlTimingLogger.isDebugEnabled()));

			} else if (sqlTimingLogger.isWarnEnabled()) {
				if (Properties.isSqlTimingWarnThresholdEnabled() && execTime >= Properties.getSqlTimingWarnThresholdMsec()) {
					sqlTimingLogger.warn(buildSqlTimingDump(spy, execTime, methodCall, sql, sqlTimingLogger.isDebugEnabled()));

				} else if (sqlTimingLogger.isDebugEnabled()) {
					sqlTimingLogger.debug(buildSqlTimingDump(spy, execTime, methodCall, sql, true));

				} else if (sqlTimingLogger.isInfoEnabled()) {
					sqlTimingLogger.info(buildSqlTimingDump(spy, execTime, methodCall, sql, false));
				}
			}
		}
	}

	@Override
	public boolean isResultSetCollectionEnabled() {
		return resultSetTableLogger.isInfoEnabled();
	}

	@Override
	public boolean isResultSetCollectionEnabledWithUnreadValueFillIn() {
		return resultSetTableLogger.isDebugEnabled();
	}

	private boolean shouldSqlBeLogged(String sql) {
		if (sql == null) {
			return false;
		}
		sql = sql.trim();
		if (sql.length() < 6) {
			return false;
		}
		sql = sql.substring(0, 6).toLowerCase();
		return
				(Properties.isDumpSqlSelect() && "select".equals(sql)) ||
						(Properties.isDumpSqlInsert() && "insert".equals(sql)) ||
						(Properties.isDumpSqlUpdate() && "update".equals(sql)) ||
						(Properties.isDumpSqlDelete() && "delete".equals(sql)) ||
						(Properties.isDumpSqlCreate() && "create".equals(sql));
	}

	private String formatSql(String sql) {
		if (sql == null) {
			return null;
		}
		if (Properties.isSqlTrim()) {
			sql = sql.trim();
		}

		if (isDDL(sql)) {
			return formatDDL(sql);
		}

		String formatSQL = formatDML(sql);
		return commentLineFeed(formatSQL);

	}

	private String formatDDL(final String sql) {
		return FormatStyle.DDL.getFormatter().format(sql);
	}

	private String formatDML(final String sql) {
		return FormatStyle.BASIC.getFormatter().format(sql);
	}

	private String removeFirstLineFeedAndTabIndent(final String formatSQL) {
		return formatSQL
				.replaceAll("^\n    ", "") // 첫줄 개행처리 및 탭처리 삭제
				.replaceAll("\n        ", "\n    ");
	}

	private String commentLineFeed(final String formatSQL) {
		return formatSQL.replaceAll("(/\\*\\* com.*. \\- .*\\*/)", "$1\n  ");
	}

	private boolean isDDL(String sql) {
		return sql.indexOf("create") > -1 || sql.indexOf("alter") > -1;
	}

	private String buildSqlTimingDump(Spy spy, long execTime, String methodCall, String sql, boolean debugInfo) {
		StringBuffer out = new StringBuffer();
		out.append(formatSql(sql));
//		out.append(sql);
		out.append(nl);
		out.append(" {executed in ");
		out.append(execTime);
		out.append(" msec}");
		return out.toString();
	}

	private static String getDebugInfo() {
		Throwable t = new Throwable();
		t.fillInStackTrace();
		StackTraceElement[] stackTrace = t.getStackTrace();

		if (stackTrace != null) {
			String className;
			StringBuffer dump = new StringBuffer();

			if (Properties.isDumpFullDebugStackTrace()) {
				boolean first = true;
				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (!className.startsWith("net.sf.log4jdbc")) {
						if (first) {
							first = false;
						} else {
							dump.append("  ");
						}
						dump.append("at ");
						dump.append(stackTrace[i]);
						dump.append(nl);
					}
				}
			} else {
				dump.append(" ");
				int firstLog4jdbcCall = 0;
				int lastApplicationCall = 0;

				for (int i = 0; i < stackTrace.length; i++) {
					className = stackTrace[i].getClassName();
					if (className.startsWith("net.sf.log4jdbc")) {
						firstLog4jdbcCall = i;
					} else if (Properties.isTraceFromApplication() &&
							Pattern.matches(Properties.getDebugStackPrefix(), className)) {
						lastApplicationCall = i;
						break;
					}
				}
				int j = lastApplicationCall;

				if (j == 0) {
					j = 1 + firstLog4jdbcCall;
				}

				dump.append(stackTrace[j].getClassName()).append(".").append(stackTrace[j].getMethodName()).append("(").
						append(stackTrace[j].getFileName()).append(":").append(stackTrace[j].getLineNumber()).append(")");
			}

			return dump.toString();
		}
		return null;
	}

	@Override
	public void debug(String msg) {
	}

	@Override
	public void connectionOpened(Spy spy, long execTime) {
	}

	@Override
	public void connectionClosed(Spy spy, long execTime) {
	}

	@Override
	public void connectionAborted(Spy spy, long execTime) {
	}

	@Override
	public void methodReturned(Spy spy, String methodCall, String returnMsg) {
	}

	@Override
	public void constructorReturned(Spy spy, String constructionInfo) {
	}

	@Override
	public void sqlOccurred(Spy spy, String methodCall, String sql) {
	}

}
