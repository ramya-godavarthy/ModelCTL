package modelCTL_java.Model;

public class BinarySymbolInfo {
    private boolean binary;
    private String leftExpression;
    private String rightExpression;
    private CtlFormula.TypeSAT typeSat;

    public BinarySymbolInfo() {
        super();
    }

    public BinarySymbolInfo(boolean isBinary, String leftExpression, String rightExpression) {
        super();
        this.binary = isBinary;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    public BinarySymbolInfo(boolean binary, String leftExpression, String rightExpression, CtlFormula.TypeSAT typeSat) {
        super();
        this.binary = binary;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
        this.typeSat = typeSat;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean isBinary) {
        this.binary = isBinary;
    }

    public String getLeftExpression() {
        return leftExpression.trim();
    }

    public void setLeftExpression(String leftExpression) {
        this.leftExpression = leftExpression;
    }

    public String getRightExpression() {
        return rightExpression.trim();
    }

    public void setRightExpression(String rightExpression) {
        this.rightExpression = rightExpression;
    }

    public CtlFormula.TypeSAT getTypeSat() {
        return typeSat;
    }

    public void setTypeSat(CtlFormula.TypeSAT typeSat) {
        this.typeSat = typeSat;
    }

    public static class TypeSatDetails {

        private boolean isBinary;
        private String leftExpression;
        private String rightExpression;

        public TypeSatDetails() {
        }

        public boolean isBinary() {
            return this.isBinary;
        }

        public void setBinary(boolean isBinary) {
            this.isBinary = isBinary;
        }

        public String getLeftExpression() {
            return this.leftExpression;
        }

        public void setLeftExpression(String leftExpression) {
            this.leftExpression = leftExpression;
        }

        public String getRightExpression() {
            return this.rightExpression;
        }

        public void setRightExpression(String rightExpression) {
            this.rightExpression = rightExpression;
        }
    }
}

