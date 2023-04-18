package com.example.awssqspilot.domain.event;


import com.sun.istack.NotNull;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

@Getter
@Builder(builderMethodName = "entityBuilder", toBuilder = true)
@NoArgsConstructor @AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity @Table(name = "APPLICATION_EVENT", catalog = "MARKETBOM2_SCHM")
public class ApplicationEvent implements Persistable<String> {

	@Id
	@Column(name = "EVENT_ID", nullable = false)
	private String eventId;

	@Column(name = "EVENT_GROUP_ID", nullable = false)
	private String eventGroupId;

	@Column(name = "BIZ_GROUP_NO", nullable = false)
	private Long bizGroupNo;

	@Column(name = "BIZ_CD", nullable = false)
	private String bizCd;

	@Convert(converter = EventStatusConverter.class)
	@Column(name = "EVENT_STATUS", columnDefinition = "TINYINT", length = 1, nullable = false)
	private EventStatus eventStatus;

	@Column(name = "EVENT_TYPE", nullable = false) @Enumerated(EnumType.STRING)
	private EventType eventType;

	@Column(name = "EVENT_PAYLOAD", columnDefinition = "JSON")
	private String eventPayload;

	@Column(name = "REG_NO", nullable = false)
	private Long regNo = 0L;

	@Column(name = "REG_DT", nullable = false)
	private LocalDateTime regDt;

	@Column(name = "UPD_DT", nullable = false)
	private LocalDateTime updDt;

	@Transient
	private String a;

	public static ApplicationEvent createEvent(
			@NotNull final String eventId,
			@NotNull final String eventGroupId,
			@NotNull final Long bizGroupNo,
			@NotNull final String bizCd,
			@NotNull final EventType eventType,
			@NotNull final String eventPayload
	) {
		return entityBuilder()
				.eventId(eventId)
				.eventGroupId(eventGroupId)
				.bizGroupNo(bizGroupNo)
				.bizCd(bizCd)
				.eventStatus(EventStatus.PUBLISHED)
				.eventType(eventType)
				.eventPayload(eventPayload)
				.regNo(0L)
				.regDt(LocalDateTime.now())
				.updDt(LocalDateTime.now())
				.build();
	}

	@Override public String getId() {
		return eventId;
	}

	@Override public boolean isNew() {
		return a == null;
	}

	public void onComplete() {
		this.eventStatus = EventStatus.COMPLETED;
	}
}
