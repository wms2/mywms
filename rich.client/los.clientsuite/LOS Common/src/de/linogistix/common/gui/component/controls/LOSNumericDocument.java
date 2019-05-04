/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.common.gui.component.controls;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class LOSNumericDocument extends PlainDocument {

    private static final long serialVersionUID = 1L;
    protected static DecimalFormat defaultFormat = new DecimalFormat();
    protected LOSNumericDocumentListener insertListener;
    protected DecimalFormat format;
    protected char decimalSeparator;
    protected char groupingSeparator;
    protected String positivePrefix;
    protected String negativePrefix;
    protected int positivePrefixLen;
    protected int negativePrefixLen;
    protected String positiveSuffix;
    protected String negativeSuffix;
    protected int positiveSuffixLen;
    protected int negativeSuffixLen;
    protected ParsePosition parsePos = new ParsePosition(0);
    protected boolean isUserInput = true;

    public LOSNumericDocument() {
        this(null);
    }

    public LOSNumericDocument(DecimalFormat format) {
        setFormat(format);
    }

    public LOSNumericDocument(AbstractDocument.Content content, DecimalFormat format) {

        super(content);
        setFormat(format);

        try {
            format.parseObject(content.getString(0, content.length()), parsePos);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Initial content not a valid number");
        }

        if (parsePos.getIndex() != content.length() - 1) {
            throw new IllegalArgumentException(
                    "Initial content not a valid number");
        }
    }

    public void setFormat(DecimalFormat fmt) {
        this.format = fmt != null ? fmt : (DecimalFormat) defaultFormat.clone();

        decimalSeparator = format.getDecimalFormatSymbols().getDecimalSeparator();
        groupingSeparator = format.getDecimalFormatSymbols().getGroupingSeparator();
        positivePrefix = format.getPositivePrefix();
        positivePrefixLen = positivePrefix.length();
        negativePrefix = format.getNegativePrefix();
        negativePrefixLen = negativePrefix.length();
        positiveSuffix = format.getPositiveSuffix();
        positiveSuffixLen = positiveSuffix.length();
        negativeSuffix = format.getNegativeSuffix();
        negativeSuffixLen = negativeSuffix.length();
    }

    public DecimalFormat getFormat() {
        return format;
    }

    public BigDecimal getValue() throws ParseException {
        try {
            String content = getText(0, getLength());
            parsePos.setIndex(0);
            format.setParseBigDecimal(true);
            BigDecimal result = (BigDecimal) format.parse(content, parsePos);
            if (parsePos.getIndex() != getLength()) {
                throw new ParseException("Not a valid number: " + content, 0);
            }

            return result;
        } catch (BadLocationException e) {
            throw new ParseException("Not a valid number", 0);
        }
    }

    public void insertString(int offset, String str, AttributeSet a)
            throws BadLocationException {

        if (!isUserInput) {
            super.insertString(offset, str, a);
            return;
        }

        if (str.length() == 1 && str.toCharArray()[0] == groupingSeparator) {
            return;
        }


        if (str == null || str.length() == 0) {
            return;
        }
        
        Content content = getContent();
        int length = content.length();
        int originalLength = length;

        if (str.length() == 1 && str.toCharArray()[0] == decimalSeparator) {

            if (offset != length - 1) {
                insertListener.insertFailed(this, offset, str, a);
                return;
            } else if (offset == length - 1 && getFormat().getMaximumFractionDigits() == 0) {
                insertListener.insertFailed(this, offset, str, a);
                return;
            }
        }

        parsePos.setIndex(0);

        // Create the result of inserting the new data,
        // but ignore the trailing newline
        String targetString = content.getString(0, offset) + str + content.getString(offset, length - offset - 1);

        // do not allow trailing zeros
        if(targetString.startsWith("0")
		   && targetString.length() > 1
		   && targetString.charAt(1) != decimalSeparator)
        {
            return;
        }
        
        if(targetString.startsWith("-0")
		   && targetString.length() > 2
		   && targetString.charAt(2) != decimalSeparator)
        {
            return;
        }
        
        // Parse the input string and check for errors
        do {
            boolean gotPositive = targetString.startsWith(positivePrefix);
            boolean gotNegative = targetString.startsWith(negativePrefix);

            length = targetString.length();

            // If we have a valid prefix, the parse fails if the
            // suffix is not present and the error is reported
            // at index 0. So, we need to add the appropriate
            // suffix if it is not present at this point.
            if (gotPositive == true || gotNegative == true) {
                String suffix;
                int suffixLength;
                int prefixLength;

                if (gotPositive == true && gotNegative == true) {
                    // This happens if one is the leading part of
                    // the other - e.g. if one is "(" and the other "(("
                    if (positivePrefixLen > negativePrefixLen) {
                        gotNegative = false;
                    } else {
                        gotPositive = false;
                    }
                }

                if (gotPositive == true) {
                    suffix = positiveSuffix;
                    suffixLength = positiveSuffixLen;
                    prefixLength = positivePrefixLen;
                } else {
                    // Must have the negative prefix
                    suffix = negativeSuffix;
                    suffixLength = negativeSuffixLen;
                    prefixLength = negativePrefixLen;
                }

                // If the string consists of the prefix alone,
                // do nothing, or the result won't parse.
                if (length == prefixLength) {
                    break;
                }

                // We can't just add the suffix, because part of it
                // may already be there. For example, suppose the
                // negative prefix is "(" and the negative suffix is
                // "$)". If the user has typed "(345$", then it is not
                // correct to add "$)". Instead, only the missing part
                // should be added, in this case ")".
                if (targetString.endsWith(suffix) == false) {
                    int i;
                    for (i = suffixLength - 1; i > 0; i--) {
                        if (targetString.regionMatches(length - i, suffix, 0, i)) {
                            targetString += suffix.substring(i);
                            break;
                        }
                    }

                    if (i == 0) {
                        // None of the suffix was present
                        targetString += suffix;
                    }

                    length = targetString.length();
                }
            }

            format.parse(targetString, parsePos);

            int endIndex = parsePos.getIndex();
            if (endIndex == length) {
                // Number is acceptable

                int decimalIndex = targetString.indexOf(decimalSeparator);
                int maxDigits = getFormat().getMaximumFractionDigits();
                if (decimalIndex == -1 ||
                        (originalLength - 1 - decimalIndex <= maxDigits)) {
                    break;
                }

            }

            // Parse ended early
            // Since incomplete numbers don't always parse, try
            // to work out what went wrong.
            // First check for an incomplete positive prefix
            if (positivePrefixLen > 0 && endIndex < positivePrefixLen && length <= positivePrefixLen && targetString.regionMatches(0, positivePrefix, 0, length)) {
                break; // Accept for now

            }

            // Next check for an incomplete negative prefix
            if (negativePrefixLen > 0 && endIndex < negativePrefixLen && length <= negativePrefixLen && targetString.regionMatches(0, negativePrefix, 0, length)) {
                break; // Accept for now

            }

            // No more corrections to make: must be an error
            if (insertListener != null) {
                insertListener.insertFailed(this, offset, str, a);
            }
            return;
        } while (true == false);

        // Finally, add to the model
        super.insertString(offset, str, a);

        insertListener.insertSucceeded(this, targetString, offset);
    }

    public void setInsertListener(LOSNumericDocumentListener l) {
        if (insertListener == null) {
            insertListener = l;
            return;
        }
        throw new IllegalArgumentException(
                "InsertErrorListener already registered");
    }

    public void removeErrorListener(LOSNumericDocumentListener l) {
        if (insertListener == l) {
            insertListener = null;
        }
    }

    public boolean isUserInput() {
        return isUserInput;
    }

    public void setUserInput(boolean isUserInput) {
        this.isUserInput = isUserInput;
    }
}
