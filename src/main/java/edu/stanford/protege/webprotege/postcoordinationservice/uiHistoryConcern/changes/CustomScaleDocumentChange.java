package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

public class CustomScaleDocumentChange {
    private final String postCoordinationAxis;
    private final String postCoordinationName;
    private final int sortingCode;

    private CustomScaleDocumentChange(String postCoordinationAxis,
                                      String postCoordinationName,
                                      int sortingCode) {
        this.postCoordinationAxis = postCoordinationAxis;
        this.postCoordinationName = postCoordinationName;
        this.sortingCode = sortingCode;
    }

    public static CustomScaleDocumentChange create(String postCoordinationAxis,
                                                   String postCoordinationName,
                                                   int sortingCode) {
        return new CustomScaleDocumentChange(postCoordinationAxis, postCoordinationName, sortingCode);
    }

    public String getPostCoordinationAxis() {
        return postCoordinationAxis;
    }

    public String getPostCoordinationName() {
        return postCoordinationName;
    }

    public int getSortingCode() {
        return sortingCode;
    }
}
