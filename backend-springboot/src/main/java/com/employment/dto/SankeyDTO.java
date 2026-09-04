package com.employment.dto;

import lombok.Data;

import java.util.List;

@Data
public class SankeyDTO {
    private List<SankeyNode> nodes;
    private List<SankeyLink> links;

    @Data
    public static class SankeyNode {
        private String name;

        public SankeyNode() {}

        public SankeyNode(String name) {
            this.name = name;
        }
    }

    @Data
    public static class SankeyLink {
        private String source;
        private String target;
        private Long value;

        public SankeyLink() {}

        public SankeyLink(String source, String target, Long value) {
            this.source = source;
            this.target = target;
            this.value = value;
        }
    }
}
