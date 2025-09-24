package edu.ccrm.domain;

public enum Grade {
    S(10), A(9), B(8), C(7), D(6), E(5), F(0);

    private final int points;
    Grade(int p) { this.points = p; }
    public int getPoints() { return points; }

    public static Grade fromScore(int sc) {
        if (sc >= 90) return S;
        if (sc >= 80) return A;
        if (sc >= 70) return B;
        if (sc >= 60) return C;
        if (sc >= 50) return D;
        if (sc >= 40) return E;
        return F;
    }
}
