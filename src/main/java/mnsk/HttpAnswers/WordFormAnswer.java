package mnsk.HttpAnswers;

import java.util.List;
import java.util.Map;

public class WordFormAnswer {
    public class AnswerElement {
        private String pos;
        private String lemma;
        private Map<String, List<String>> forms_query;
        private String original;
        private List<Map<String, List<String>>> forms;
        private List<String> vector;

        public List<Map<String, List<String>>> getForms() { return forms; }
        public Map<String, List<String>> getForms_query() { return forms_query; }
        public List<String> getVector() { return vector; }
        public String getLemma() { return lemma; }
        public String getOriginal() { return original; }
        public String getPos() { return pos; }

        public void setForms(List<Map<String, List<String>>> forms) { this.forms = forms; }
        public void setForms_query(Map<String, List<String>> forms_query) { this.forms_query = forms_query; }
        public void setLemma(String lemma) { this.lemma = lemma; }
        public void setOriginal(String original) { this.original = original; }
        public void setPos(String pos) { this.pos = pos; }
        public void setVector(List<String> vector) { this.vector = vector; }
    }

    private Integer code;
    private String msg;
    private Map<Integer, AnswerElement> response;

    public String getMsg() { return msg; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public void setMsg(String msg) { this.msg = msg; }
    public Map<Integer, AnswerElement> getResponse() { return response; }
    public void setResponse(Map<Integer, AnswerElement> response) { this.response = response; }
}
