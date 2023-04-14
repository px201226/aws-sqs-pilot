package com.example.awssqspilot.domain.event;

public interface EventSource {

	String getEventId();

	String getEventGroupId();

	Long getBizGroupNo();

	String getBizCd();
}
