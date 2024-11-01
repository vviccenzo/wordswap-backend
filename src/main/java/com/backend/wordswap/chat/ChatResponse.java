package com.backend.wordswap.chat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse {
	
    private Candidates[] candidates;

    public Candidates[] getCandidates() {
        return candidates;
    }

    public void setCandidates(Candidates[] candidates) {
        this.candidates = candidates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidates {
        private Content content;

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private Part[] parts;

        public Part[] getParts() {
            return parts;
        }

        public void setParts(Part[] parts) {
            this.parts = parts;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}

