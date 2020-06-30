package com.example.keval.keval.Adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;
import android.text.TextUtils;

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class AdapterPrintInvoice extends PrintDocumentAdapter {

    JSONArray infoArray = new JSONArray();
    Activity activity;
    JSONObject onlyShowValue;
    private PrintedPdfDocument printedPdfDocument;
    int topMarginAdd = 0;
    int bottomMarginAdd = 0;
    int totalPages = 0;
    ArrayList<Integer> containsPage = new ArrayList<>();
    int totalSize = 0;
    int addSizePor = 9;
    int addSizeLand = 5;
    PrintAttributes attributes = null;
    int start = 0;
    int end = 0;
    PrintAttributes.MediaSize pageSize;

    public AdapterPrintInvoice(Activity activity, JSONObject onlyShowValue) {
        this.activity = activity;
        this.onlyShowValue = onlyShowValue;
        infoArray = onlyShowValue.optJSONArray("info");
        totalSize = infoArray.length();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        printedPdfDocument = new PrintedPdfDocument(activity, newAttributes);
        attributes = newAttributes;
        pageSize = attributes.getMediaSize();
        onCreateValue();

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        int pages = computePageCount();

        if (pages > 0) {
            PrintDocumentInfo printDocumentInfo;

            if (pageSize.isPortrait())
                printDocumentInfo = new PrintDocumentInfo.Builder(activity.getResources().getString(R.string.billing) + "_" + CommonUtils.dateAndIdCombine(onlyShowValue.optString("billDate"), onlyShowValue.optString("billNo")) + "_Portrait")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(pages)
                        .build();
            else
                printDocumentInfo = new PrintDocumentInfo.Builder(activity.getResources().getString(R.string.billing) + "_" + CommonUtils.dateAndIdCombine(onlyShowValue.optString("billDate"), onlyShowValue.optString("billNo")) + "_Landscape")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(pages)
                        .build();

            callback.onLayoutFinished(printDocumentInfo, true);

        } else
            callback.onLayoutFailed("Page Count calculation failed.");
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {

        for (int i = 1; i <= totalPages; i++) {

            if (containsPages(i)) {
                PdfDocument.Page page = printedPdfDocument.startPage(i);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    printedPdfDocument.close();
                    printedPdfDocument = null;
                    return;
                }

                drawNewPage(page);

                calculateSize();
                printedPdfDocument.finishPage(page);
            }
        }

        try {
            printedPdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        }
        callback.onWriteFinished(pages);
    }

    public void onCreateValue() {
        addSizePor = 9;
        addSizeLand = 5;

        if (pageSize.isPortrait()) {
            start = 0;
            end = addSizePor;
        } else {
            start = 0;
            end = addSizeLand;
        }
    }

    public void calculateSize() {

        if (pageSize.isPortrait()) {
            start = start + addSizePor;
            end = end + addSizePor;
        } else {
            start = start + addSizeLand;
            end = end + addSizeLand;
        }
    }

    public boolean containsPages(int pagePosition) {

        if (!containsPage.contains(pagePosition)) {
            containsPage.add(pagePosition);
            return true;
        } else
            return false;
    }

    public int computePageCount() {

        containsPage = new ArrayList<>();

        if (pageSize.isPortrait()) {
            if (totalSize <= addSizePor)
                totalPages = 1;
            else if (totalSize % addSizePor == 0)
                totalPages = totalSize / addSizePor;
            else
                totalPages = (totalSize / addSizePor) + 1;
        } else {
            if (totalSize <= addSizeLand)
                totalPages = 1;
            else if (totalSize % addSizeLand == 0)
                totalPages = totalSize / addSizeLand;
            else
                totalPages = (totalSize / addSizeLand) + 1;
        }
        return totalPages;

    }

    public void drawNewPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();
        topMarginAdd = 0;
        bottomMarginAdd = 0;

        int pageWidth = page.getInfo().getPageWidth();
        int pageHeight = page.getInfo().getPageHeight();
        int halfPageWidth = (pageWidth / 2);
        int halfPageHeight = (pageHeight / 2);
        int leftMargin = 30;

        Paint paint = new Paint();
        paint.setTypeface(null);

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        paint.setTextSize(20);
        paint.setTypeface(Typeface.create("BOLD_ITALIC", Typeface.BOLD_ITALIC));
        paint.setFakeBoldText(true);
        canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " " + onlyShowValue.optString("billNo"), pageWidth - 160, topMargin(72), paint);

        paint.setColor(activity.getResources().getColor(R.color.gray_staff_name));
        paint.setTypeface(null);
        paint.setTextSize(13);
        canvas.drawText(activity.getResources().getString(R.string.billing) + " Date ", pageWidth - 230, topMargin(30), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setFakeBoldText(false);
        canvas.drawText(new SimpleDateFormat("MMMM dd, yyyy").format(CommonUtils.convertStringToDate(onlyShowValue.optString("billDate"))), pageWidth - 140, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        canvas.drawRect(leftMargin, topMargin(15), pageWidth - leftMargin, topMargin(5), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        canvas.drawLine(leftMargin, topMargin(5), pageWidth - leftMargin, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create("ITALIC", Typeface.ITALIC));
        paint.setTypeface(null);
        canvas.drawText(activity.getResources().getString(R.string.billing) + " Holder Name", leftMargin, topMargin(20), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setTextSize(13);
        canvas.drawText(onlyShowValue.optString("customerName"), leftMargin, topMargin(20), paint);

        if (pageSize.isPortrait()) {

            paint.setColor(activity.getResources().getColor(R.color.dark_gray));
            canvas.drawRect(leftMargin, topMargin(80), leftMargin + 25, pageHeight - bottomMargin(160), paint);
            canvas.drawRect(leftMargin + 170, topMargin(0), leftMargin + 240, pageHeight - bottomMargin(0), paint);
            canvas.drawRect(leftMargin + 310, topMargin(0), leftMargin + 380, pageHeight - bottomMargin(0), paint);
            canvas.drawRect(pageWidth - 130, topMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);

            paint.setColor(activity.getResources().getColor(R.color.lightblue));

            canvas.drawLine(leftMargin, topMargin(-25), pageWidth - leftMargin, topMargin(0), paint);
            paint.setTextSize(8);
            paint.setTypeface(Typeface.create("BOLD", Typeface.BOLD));
            canvas.drawText("NO.", leftMargin, topMargin(15), paint);
            canvas.drawText("PRODUCT NAME", leftMargin + 25, topMargin(0), paint);
            canvas.drawText("QUANTITY", leftMargin + 170, topMargin(0), paint);
            canvas.drawText("RATE", leftMargin + 240, topMargin(0), paint);
            canvas.drawText("GST", leftMargin + 310, topMargin(0), paint);
            canvas.drawText("Discount", leftMargin + 380, topMargin(0), paint);
            canvas.drawText("AMOUNT (" + activity.getResources().getString(R.string.currency_india) + ")", pageWidth - 130, topMargin(0), paint);
            canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);

            for (int i = start; i < end; i++) {
                JSONObject infoObject = infoArray.optJSONObject(i);
                String serialNo = (i + 1) + "";

                if (i <= totalSize - 1) {
                    paint.setColor(activity.getResources().getColor(R.color.black));
                    paint.setTextSize(11);

                    canvas.drawText(serialNo, leftMargin, topMargin(20), paint);
                    canvas.drawText(infoObject.optString("itemName"), leftMargin + 25, topMargin(0), paint);
                    canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemQuantity")), leftMargin + 170, topMargin(0), paint);
                    canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemPrice")), leftMargin + 240, topMargin(0), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemTaxInAmount")) || infoObject.optDouble("itemTaxInAmount") > 0)
                        canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemTaxInAmount")), leftMargin + 310, topMargin(0), paint);
                    else
                        canvas.drawText("0.00", leftMargin + 310, topMargin(0), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemDiscountInAmount")) || infoObject.optDouble("itemDiscountInAmount") > 0)
                        canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemDiscountInAmount")), leftMargin + 380, topMargin(0), paint);
                    else
                        canvas.drawText("0.00", leftMargin + 380, topMargin(0), paint);

                    canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(infoObject.optDouble("itemTotal")), pageWidth - 130, topMargin(0), paint);

                    paint.setColor(activity.getResources().getColor(R.color.tab_text_unselected));
                    paint.setTextSize(8);

                    if (!TextUtils.isEmpty(infoObject.optString("itemTax")) || infoObject.optDouble("itemTax") > 0)
                        canvas.drawText("(" + CommonUtils.getDecimal(infoObject.optDouble("itemTax")) + "%)", leftMargin + 310, topMargin(10), paint);
                    else
                        canvas.drawText("(0.00%)", leftMargin + 310, topMargin(10), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemDiscount")) || infoObject.optDouble("itemDiscount") > 0)
                        canvas.drawText("(" + CommonUtils.getDecimal(infoObject.optDouble("itemDiscount")) + "%)", leftMargin + 380, topMargin(0), paint);
                    else
                        canvas.drawText("(0.00%)", leftMargin + 380, topMargin(0), paint);

                    if (i != end - 1 && i != infoArray.length() - 1) {
                        paint.setColor(activity.getResources().getColor(R.color.lightblue));
                        canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);
                    }

                } else {
                    break;
                }
            }
        } else {

            paint.setColor(activity.getResources().getColor(R.color.dark_gray));
            canvas.drawRect(leftMargin, topMargin(80), leftMargin + 35, pageHeight - bottomMargin(160), paint);
            canvas.drawRect(leftMargin + 200, topMargin(0), leftMargin + 300, pageHeight - bottomMargin(0), paint);
            canvas.drawRect(leftMargin + 400, topMargin(0), leftMargin + 500, pageHeight - bottomMargin(0), paint);
            canvas.drawRect(leftMargin + 600, topMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);

            paint.setColor(activity.getResources().getColor(R.color.lightblue));

            canvas.drawLine(leftMargin, topMargin(-25), pageWidth - leftMargin, topMargin(0), paint);
            paint.setTextSize(8);
            paint.setTypeface(Typeface.create("BOLD", Typeface.BOLD));
            canvas.drawText("NO.", leftMargin, topMargin(15), paint);
            canvas.drawText("PRODUCT NAME", leftMargin + 35, topMargin(0), paint);
            canvas.drawText("QUANTITY", leftMargin + 200, topMargin(0), paint);
            canvas.drawText("RATE", leftMargin + 300, topMargin(0), paint);
            canvas.drawText("GST", leftMargin + 400, topMargin(0), paint);
            canvas.drawText("Discount", leftMargin + 500, topMargin(0), paint);
            canvas.drawText("AMOUNT (" + activity.getResources().getString(R.string.currency_india) + ")", leftMargin + 600, topMargin(0), paint);
            canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);

            for (int i = start; i < end; i++) {
                JSONObject infoObject = infoArray.optJSONObject(i);
                String serialNo = (i + 1) + "";

                if (i <= totalSize - 1) {
                    paint.setColor(activity.getResources().getColor(R.color.black));
                    paint.setTextSize(11);

                    canvas.drawText(serialNo, leftMargin, topMargin(20), paint);
                    canvas.drawText(infoObject.optString("itemName"), leftMargin + 35, topMargin(0), paint);
                    canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemQuantity")), leftMargin + 200, topMargin(0), paint);
                    canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemPrice")), leftMargin + 300, topMargin(0), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemTaxInAmount")) || infoObject.optDouble("itemTaxInAmount") > 0)
                        canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemTaxInAmount")), leftMargin + 400, topMargin(0), paint);
                    else
                        canvas.drawText("0.00", leftMargin + 400, topMargin(0), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemDiscountInAmount")) || infoObject.optDouble("itemDiscountInAmount") > 0)
                        canvas.drawText(CommonUtils.getDecimal(infoObject.optDouble("itemDiscountInAmount")), leftMargin + 500, topMargin(0), paint);
                    else
                        canvas.drawText("0.00", leftMargin + 500, topMargin(0), paint);

                    canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(infoObject.optDouble("itemTotal")), leftMargin + 600, topMargin(0), paint);

                    paint.setColor(activity.getResources().getColor(R.color.tab_text_unselected));
                    paint.setTextSize(8);

                    if (!TextUtils.isEmpty(infoObject.optString("itemTax")) || infoObject.optDouble("itemTax") > 0)
                        canvas.drawText("(" + CommonUtils.getDecimal(infoObject.optDouble("itemTax")) + "%)", leftMargin + 400, topMargin(10), paint);
                    else
                        canvas.drawText("(0.00%)", leftMargin + 400, topMargin(10), paint);

                    if (!TextUtils.isEmpty(infoObject.optString("itemDiscount")) || infoObject.optDouble("itemDiscount") > 0)
                        canvas.drawText("(" + CommonUtils.getDecimal(infoObject.optDouble("itemDiscount")) + "%)", leftMargin + 500, topMargin(0), paint);
                    else
                        canvas.drawText("(0.00%)", leftMargin + 500, topMargin(0), paint);

                    if (i != end - 1 && i != infoArray.length() - 1) {
                        paint.setColor(activity.getResources().getColor(R.color.lightblue));
                        canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);
                    }

                } else {
                    break;
                }
            }
        }
        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        canvas.drawLine(leftMargin, pageHeight - bottomMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);

        paint.setTypeface(Typeface.create("BOLD", Typeface.BOLD));
        paint.setTextSize(13);
        canvas.drawText("Authorized Signatory".toUpperCase(), leftMargin, pageHeight - bottomMargin(-20), paint);
        paint.setTextSize(10);

        if (page.getInfo().getPageNumber() == totalPages) {

            canvas.drawText("Product Total".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(0), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billAmount")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " Discount".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billDiscountInAmount")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawText("Shipping Charge".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billShipping")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " Total".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billTotal")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawText("Amount Paid".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billAddPayment")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawText(onlyShowValue.optString("billTransactionStatusAtInvoice").toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billTransactionAtInvoice")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

            canvas.drawLine((float) pageWidth - 300, pageHeight - bottomMargin(-10), (float) pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);
            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " Due".toUpperCase(), pageWidth - 300, pageHeight - bottomMargin(-20), paint);
            canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(onlyShowValue.optDouble("billLeft")), pageWidth - 130, pageHeight - bottomMargin(0), paint);

        }
    }

    public int topMargin(int addMargin) {
        topMarginAdd = topMarginAdd + addMargin;
        return topMarginAdd;
    }

    public int bottomMargin(int addMargin) {
        bottomMarginAdd = bottomMarginAdd + addMargin;
        return bottomMarginAdd;
    }
}