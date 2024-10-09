package edu.stanford.protege.webprotege.postcoordinationservice.uiHistoryConcern.changes;

public class SpecDocumentChange {
    private final String linearizationViewIri;
    private final String linearizationViewName;
    private final String linearizationViewId;
    private final String sortingCode;

    private SpecDocumentChange(String linearizationViewIri,
                               String linearizationViewName,
                               String linearizationViewId,
                               String sortingCode) {
        this.linearizationViewIri = linearizationViewIri;
        this.linearizationViewName = linearizationViewName;
        this.linearizationViewId = linearizationViewId;
        this.sortingCode = sortingCode;
    }

    public static SpecDocumentChange create(String linearizationViewIri,
                                            String linearizationViewName,
                                            String linearizationViewId,
                                            String sortingCode) {
        return new SpecDocumentChange(linearizationViewIri, linearizationViewName, linearizationViewId, sortingCode);
    }

    public String getLinearizationViewIri() {
        return linearizationViewIri;
    }

    public String getLinearizationViewName() {
        return linearizationViewName;
    }

    public String getLinearizationViewId() {
        return linearizationViewId;
    }

    public String getSortingCode() {
        return sortingCode;
    }
}
