package com.terran4j.commons.test.armq;

import com.terran4j.commons.armq.MessageEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@Data
@MessageEntity
public class Normal {

    private Long type;

    private String msg;

    private List<Map<String, String>> records;
}
