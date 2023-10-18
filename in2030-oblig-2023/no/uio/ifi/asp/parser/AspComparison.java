package no.uio.ifi.asp.parser;

import java.util.ArrayList;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;

public class AspComparison extends AspSyntax {

    ArrayList<AspTerm> terms = new ArrayList<>();
    ArrayList<AspCompOpr> compOprs = new ArrayList<>();

    protected AspComparison(int n) {
        super(n);
    }

    static AspComparison parse(Scanner s) {

        enterParser("comparison");

        AspComparison c = new AspComparison(s.curLineNum());
        c.terms.add(AspTerm.parse(s));

        // As long as currentoken is a compOpr
        while (s.isCompOpr()) {
            c.compOprs.add(AspCompOpr.parse(s));
            c.terms.add(AspTerm.parse(s));
        }

        leaveParser("comparison");
        return c;
    }

    @Override
    void prettyPrint() {

        for (int i = 0; i < terms.size(); i++) {
            terms.get(i).prettyPrint();

            if (i <= compOprs.size() - 1) {
                compOprs.get(i).prettyPrint();
            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = terms.get(0).eval(curScope);
        for (int i = 1; i < terms.size(); ++i) {
            v = terms.get(i).eval(curScope);

            TokenKind k = compOprs.get(i - 1).coVal;
            switch (k) {
                case lessToken:
                    v = v.evalLess(terms.get(i).eval(curScope), this);
                    break;

                case greaterToken:
                    v = v.evalGreater(terms.get(i).eval(curScope), this);
                    break;

                case doubleEqualToken:
                    v = v.evalEqual(terms.get(i).eval(curScope), this);
                    break;

                case greaterEqualToken:
                    v = v.evalGreaterEqual(terms.get(i).eval(curScope), this);
                    break;

                case lessEqualToken:
                    v = v.evalLessEqual(terms.get(i).eval(curScope), this);
                    System.out.println("termerrr " + terms.get(i).eval(curScope));
                    System.out.println("fdujyheyhoiu4r5ugto " + v);
                    break;

                case notEqualToken:
                    v = v.evalNotEqual(terms.get(i).eval(curScope), this);
                    break;

                default:
                    Main.panic("Illegal term operator: " + k + "!");
            }

            if (v.getBoolValue("comparison", this) == false) {
                System.out.println("inside iff: " + v);
                return v;
            }

        }
        System.out.println("end return" + v);
        return v;
    }
}