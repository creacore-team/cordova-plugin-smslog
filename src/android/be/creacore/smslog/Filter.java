package be.creacore.smslog;

import android.provider.Telephony;
import java.util.Arrays;

public class Filter {
    private String name;
    private String value;
    private String operator;
    private String operation = "AND";

    public static String[] validNames = {
        Telephony.TextBasedSmsColumns.ADDRESS,
        Telephony.TextBasedSmsColumns.BODY,
        Telephony.TextBasedSmsColumns.DATE,
        Telephony.TextBasedSmsColumns.DATE_SENT,
        Telephony.TextBasedSmsColumns.READ,
        Telephony.TextBasedSmsColumns.SUBSCRIPTION_ID,
        Telephony.TextBasedSmsColumns.TYPE,
    };

    public String getName() {
        return name;
    }

    public void setName(String name) throws Exception {
        if(Arrays.asList(validNames).contains(name)) {
            this.name = name;
        } else {
            throw new Exception("Invalid filter name");
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperator() {
        return operator.isEmpty() ? "=" : operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperation() {
        return this.operation;
    }
}