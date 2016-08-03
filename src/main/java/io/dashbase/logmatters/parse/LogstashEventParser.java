package io.dashbase.logmatters.parse;

import java.util.Map;

import org.apache.lucene.util.BytesRef;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import rapid.ingester.RapidIngestedData;
import rapid.ingester.RapidIngesterParser;
import rapid.parser.RapidColumn;

public class LogstashEventParser implements RapidIngesterParser
{
  private ObjectMapper mapper = new ObjectMapper();
  private Map<String, RapidColumn> schemaMap = Maps.newHashMap();
  
  static String[] META_COLS = new String[] {
    "source_host", "file", "method", "level", "thread_name", "logger_name", "class", "exception_class"
  };
  
  static String[] TEXT_COLS = new String[] {
    "class_name", "message", "stacktrace", "exception_message", "exception_class_name"
  };
  
  static String[] NUMERIC_COLS = new String[] {
    "line_number"
  };
  
  static String[] DIRECT_MAP_COLS = new String[] {
    "source_host", "file", "method", "level", "thread_name", "logger_name", "class", "line_number", "message"
  };
  
  static String[] EXCEPTION_DIRECT_MAP_COLS = new String[] {
    "stacktrace", "exception_message", "exception_class"
  };
  
  
  private static RapidColumn getMetaColumn(String name) {
    RapidColumn col = new RapidColumn();
    col.name = name;
    col.setType(RapidColumn.META_TYPE);
    return col;
  }
  
  private static RapidColumn getTextColumn(String name) {
    RapidColumn col = new RapidColumn();
    col.name = name;
    col.setType(RapidColumn.TEXT_TYPE);
    return col;
  }
  
  private static RapidColumn getNumeric(String name, boolean isDouble) {
    RapidColumn col = new RapidColumn();
    col.name = name;
    col.setType(RapidColumn.NUMERIC_TYPE);
    col.isDouble = isDouble;
    return col;
  }
  
  private static String getValue(Map<String, Object> dataMap, String name) {
    Object obj = dataMap.get(name);
    return obj == null ? null : obj.toString();
  }
  
  public LogstashEventParser()
  {
    for (String name : META_COLS) {
      schemaMap.put(name, getMetaColumn(name));  
    }
    
    for (String name: TEXT_COLS) {
      schemaMap.put(name, getTextColumn(name));
    }
    
    for (String name: NUMERIC_COLS) {
      schemaMap.put(name, getNumeric(name, false));
    }
  }

  @Override
  public boolean parse(byte[] rawContent, RapidIngestedData data, ParseContext ctx) throws Exception
  {
    Map<String, Object> dataMap = null;

    try {
      dataMap = mapper.readValue(rawContent, Map.class);
    } catch (JsonParseException parseException) {
      return false;
    }
    
    if (data.payload == null) {
      data.payload = new BytesRef(rawContent);
    } else {
      // possible reuse the bytes
      if (rawContent.length <= data.payload.length) {
        System.arraycopy(rawContent, 0, data.payload.bytes, data.payload.offset, rawContent.length);
        data.payload.length = rawContent.length;
      } else {
        data.payload = new BytesRef(rawContent);
      }
    }
    data.content.clear();
    
    for (String name : DIRECT_MAP_COLS) {
      String val = getValue(dataMap, name);
      if (val != null) {
        data.content.put(name, val);
      }
    }
    
    String val = data.content.get("class");
    if (val != null) {
      data.content.put("class_name", val);
    }
    
    Object exception = dataMap.get("exception");
    if (exception != null) {
      Map<String, Object> exceptionMap = (Map<String, Object>)exception;
      for (String name : EXCEPTION_DIRECT_MAP_COLS) {
        val = getValue(exceptionMap, name);
        if (val != null) {
          data.content.put(name, val);
        }
      }
    }
    
    val = data.content.get("exception_class");
    if (val != null) {
      data.content.put("exception_class_name", val);
    }
    
    return true;
  }

  @Override
  public Map<String, RapidColumn> getSchema()
  {
    return schemaMap;
  }

}
