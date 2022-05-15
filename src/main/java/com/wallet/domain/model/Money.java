package com.wallet.domain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;

/**
 * 钱模型，默认的货币类型是人民币
 * 暂时不支持不同货币之间的转换，因为会涉及实时的汇率获取
 */
public class Money implements Comparable<Money>, Serializable, Cloneable {

    private static final long serialVersionUID = -9204010508573499394L;
    /**
     * 四舍五入取整模式
     */
    public static final int DEFAULT_ROUNDING_MODE = BigDecimal.ROUND_HALF_EVEN;

    public Money(Money money) {
        this(money.getCent(), money.getCurrency());
    }

    public Money(long cent, Currency currency) {
        this.currency = checkNotNull(currency);
        this.cent = cent;
    }

    public Money(long cent) {
        this(cent, DEFAULT_CURRENCY);
    }

    public Money(String amount) {
        this(amount, Currency.getInstance(DEFAULT_CURRENCY_CODE));
    }

    /**
     * Money构造器。
     * <p>
     * <p>
     * 创建一个具有金额<code>amount</code>和指定币种的货币对象。
     * 如果金额不能转换为整数分，则使用缺省的取整模式<code>DEFAULT_ROUNDING_MODE</code>进行取整。
     *
     * @param amount   金额，以元为单位。
     * @param currency 币种
     */
    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, DEFAULT_ROUNDING_MODE);
    }

    public Money(String amount, Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    /**
     * 构造器。
     * <p>
     * <p>
     * 创建一个具有金额<code>amount</code>和指定币种的货币对象。
     * 如果金额不能转换为整数分，则使用指定的取整模式<code>roundingMode</code>取整。
     *
     * @param amount       金额，以元为单位。
     * @param currency     币种。
     * @param roundingMode 取整模式。
     */
    public Money(BigDecimal amount, Currency currency, int roundingMode) {
        this.currency = currency;
        this.cent = rounding(amount.movePointRight(currency.getDefaultFractionDigits()),
                roundingMode);
    }

    /**
     * 对BigDecimal型的值按指定取整方式取整。
     *
     * @param val          待取整的BigDecimal值
     * @param roundingMode 取整方式
     * @return 取整后的long型值
     */
    protected long rounding(BigDecimal val, int roundingMode) {
        return val.setScale(0, roundingMode).longValue();
    }


    /**
     * 货币。
     */
    private final Currency currency;
    /**
     * 数值（分）。
     */
    private final long cent;

    /**
     * 默认货币。
     */
    public static final String DEFAULT_CURRENCY_CODE = "CNY";

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_CURRENCY_CODE);

    private int hashCode;

    /**
     * 获取以通用单位为整数位的数值。比如人民币 4.22 元，对应日元 422，美元 4.22。小数点在哪里取决定货币自身属性。
     */
    public BigDecimal getAmount() {
        int fractionDigits = Math.max(0, currency.getDefaultFractionDigits());
        return BigDecimal.valueOf(cent, fractionDigits);
    }

    public String getAmountString() {
        String amount = this.getAmount().toString();
        if (amount.endsWith(".00")) {
            return amount.substring(0, amount.length() - 3);
        } else {
            return amount;
        }
    }

    /**
     * 获取将金额换算成分后的数值。
     */
    public long getCent() {
        return cent;
    }

    /**
     * 相加。
     */
    public Money add(Money other) {
        Currency otherCurrency = other.currency;
        Currency currency = this.currency;
        checkArgument(otherCurrency == currency || otherCurrency.equals(currency));
        return new Money(cent + other.cent, currency);
    }

    /**
     * 相减。
     */
    public Money subtract(Money other) {
        Currency otherCurrency = other.currency;
        Currency currency = this.currency;
        checkArgument(otherCurrency == currency || otherCurrency.equals(currency));
        return new Money(cent - other.cent, currency);
    }

    /**
     * 相乘。
     */
    public Money multiply(long val) {
        return new Money(cent * val, currency);
    }

    /**
     * 相乘。
     */
    public Money multiply(double val) {
        return new Money(Math.round(cent * val), currency);
    }

    /**
     * 相乘。
     */
    public Money multiply(BigDecimal val) {
        return multiply(val, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 相乘。
     */
    public Money multiply(BigDecimal val, int roundingMode) {
        long newCent = BigDecimal.valueOf(this.cent).multiply(val).setScale(0, roundingMode).longValue();
        return new Money(newCent, currency);
    }

    /**
     * 相除。
     */
    public Money divide(double val) {
        checkArgument(val != 0);
        return new Money(Math.round(cent / val), currency);
    }

    /**
     * 相除。
     */
    public Money divide(BigDecimal val) {
        return divide(val, DEFAULT_ROUNDING_MODE);
    }

    /**
     * 相除。
     */
    public Money divide(BigDecimal val, int roundingMode) {
        BigDecimal newCent = BigDecimal.valueOf(this.cent).divide(val, roundingMode);

        return new Money(newCent.longValue(), currency);
    }

    /**
     * 格式成字符串。
     */
    public String format() {
        return currency.getSymbol() + getAmount();
    }

    private String format(String format) {
        return String.format(format, getAmount());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        Money other = (Money) obj;
        return this.cent == other.cent && this.currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        int hashCode = this.hashCode;
        if (hashCode > 0) {
            return hashCode;
        }
        int h = 4;
        h = h * 31 + currency.hashCode();
        h = h * 31 + (int) (cent ^ (cent >>> 32));
        this.hashCode = h;
        hashCode = h;
        return hashCode;
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public int compareTo(Money o) {
        if (o == null) {
            return 1;
        }

        if (!o.currency.equals(currency)) {
            throw new IllegalArgumentException("can't compare money of different currencies");
        }

        return (int) Math.signum(cent - o.cent);
    }

    /**
     * 获取货币。
     */
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * Gets the ISO 4217 currency code of this currency.
     *
     * @return the ISO 4217 currency code of this currency.
     */
    public String getCurrencyCode() {
        return getCurrency().getCurrencyCode();
    }

    /**
     * 以中国货币格式化成字符串。如果当前货币不是人民币则报错。
     */
    public static String formatCNY(Money money) {
        checkNotNull(money);
        checkArgument("CNY".equals(money.currency.getCurrencyCode()));

        return money.format("%s元");
    }

    /**
     * 从通用单位构建，一般指“元”。
     *
     * @param yuan     金额
     * @param currency 货币
     * @return 实例
     */
    public static Money fromCommonUnit(double yuan, Currency currency) {
        int fractionDigits = Math.max(0, currency.getDefaultFractionDigits());
        long cent = Math.round(yuan * Math.pow(10, fractionDigits));
        return new Money(cent);
    }

    private static <T> T checkNotNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }

    private static void checkArgument(boolean expr) {
        if (!expr) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 是否金额大于 0。
     */
    public boolean isPositive() {
        return cent > 0;
    }

    /**
     * 0 元。
     */
    public static final Money ZERO_CNY = new Money(0);

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException var2) {
            return null;
        }
    }
}
