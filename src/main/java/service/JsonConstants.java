package service;

public class JsonConstants {
    public static final String matchAll =
            "{\n" +
                    "    \"query\": {\n" +
                    "        \"match_all\": {}\n" +
                    "    }\n" +
                    "}";

    /**
     * PUT serverlog
     */
    public static final String addCallstackToIndex =
            "{\n" +
                    "  \"mappings\": {\n" +
                    "    \"log\": {\n" +
                    "      \"properties\": {\n" +
                    "        \"callstack\": {\n" +
                    "          \"type\": \"text\"\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    /**
     * PUT serverlog/_mapping/log
     */
    public static final String getAddCallstackToType =
            "{\n" +
                    "  \"properties\": {\n" +
                    "    \"callstack\": {\n" +
                    "      \"type\": \"text\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

    /**
     * GET /serverlog/log/_search
     */
    public static final String getDocumentsByDateAsc =
            "{\n" +
                    "  \"sort\" : [\n" +
                    "        { \"date\" : {\"order\" : \"asc\"}}\n" +
                    "    ],\n" +
                    "    \"query\": {\n" +
                    "        \"match_all\": {}\n" +
                    "    }\n" +
                    "}";
}
