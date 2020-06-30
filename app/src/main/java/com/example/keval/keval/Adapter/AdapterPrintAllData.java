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

import com.example.keval.keval.R;
import com.example.keval.keval.Utils.CommonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class AdapterPrintAllData extends PrintDocumentAdapter {

    private JSONArray infoArray = new JSONArray();
    private Activity activity;
    private PrintedPdfDocument printedPdfDocument;
    private ArrayList<Integer> containsPage = new ArrayList<>();
    private String mainTotal = "";
    private String customerName = "";
    private int topMarginAdd = 0;
    private int totalPages = 0;
    private int totalSize;
    private int addSizePor = 15;
    private int addSizeLand = 9;
    private int start = 0;
    private int end = 0;
    private PrintAttributes.MediaSize pageSize;
    private int bottomMarginAdd = 0;

    public AdapterPrintAllData(Activity activity, JSONObject onlyShowValue) {
        this.activity = activity;
        infoArray = onlyShowValue.optJSONArray("data");
        mainTotal = onlyShowValue.optString("grandTotal");
        customerName = onlyShowValue.optJSONArray("profile").optJSONObject(0).optString("customerName");
        totalSize = infoArray.length();
    }

    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

        printedPdfDocument = new PrintedPdfDocument(activity, newAttributes);
        pageSize = newAttributes.getMediaSize();
        onCreateValue();

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        int pages = computePageCount();

        if (pages > 0) {

            PrintDocumentInfo printDocumentInfo;
            if (pageSize.isPortrait())

                printDocumentInfo = new PrintDocumentInfo.Builder("Statement_" + customerName + " " + CommonUtils.dateFormat(System.currentTimeMillis()) + "_Portrait")
                        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                        .setPageCount(pages)
                        .build();

            else
                printDocumentInfo = new PrintDocumentInfo.Builder("Statement_" + customerName + " " + CommonUtils.dateFormat(System.currentTimeMillis()) + "_Landscape")
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

//                drawPage(page);
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

    private void onCreateValue() {

        addSizePor = 15;
        addSizeLand = 9;

        if (pageSize.isPortrait()) {
            start = totalSize - 1;
            end = totalSize - addSizePor;
        } else {
            start = totalSize - 1;
            end = totalSize - addSizeLand;
        }
    }

    private void calculateSize() {

        if (pageSize.isPortrait()) {
            start = start - addSizePor;
            end = end - addSizePor;
        } else {
            start = start - addSizeLand;
            end = end - addSizeLand;
        }
    }

    private boolean containsPages(int pagePosition) {

        if (!containsPage.contains(pagePosition)) {
            containsPage.add(pagePosition);
            return true;
        } else
            return false;
    }

    private int computePageCount() {
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

    private void drawNewPage(PdfDocument.Page page) {
        Canvas canvas = page.getCanvas();
        topMarginAdd = 0;
        bottomMarginAdd = 0;

        int pageWidth = page.getInfo().getPageWidth();
        int pageHeight = page.getInfo().getPageHeight();
        int halfPageWidth = (pageWidth / 2);
        int halfPageHeight = (pageHeight / 2);
        int leftMargin = 30;

        Paint paint = new Paint();

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        paint.setTextSize(20);
        paint.setFakeBoldText(true);
        paint.setTypeface(Typeface.create("BOLD_ITALIC", Typeface.BOLD_ITALIC));
        canvas.drawText("Statement".toUpperCase(), pageWidth - 160, topMargin(72), paint);

        paint.setColor(activity.getResources().getColor(R.color.gray_staff_name));
        paint.setTextSize(13);
        paint.setTypeface(null);
        canvas.drawText("Statement Date ", pageWidth - 260, topMargin(30), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setFakeBoldText(false);
        canvas.drawText(new SimpleDateFormat("MMMM dd, yyyy").format(CommonUtils.convertStringToDate(CommonUtils.dateFormat(System.currentTimeMillis()))), pageWidth - 160, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        canvas.drawRect(leftMargin, topMargin(15), pageWidth - leftMargin, topMargin(5), paint);

        paint.setColor(activity.getResources().getColor(R.color.blue));
        canvas.drawLine(leftMargin, topMargin(5), pageWidth - leftMargin, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        paint.setTextSize(15);
        paint.setTypeface(Typeface.create("ITALIC", Typeface.ITALIC));
        canvas.drawText("Account Holder Name", leftMargin, topMargin(20), paint);
        paint.setTypeface(null);

        String total = mainTotal;

        if (Double.valueOf(total) < 0)
            canvas.drawText("Amount Deposit", pageWidth - 150, topMargin(0), paint);
        else
            canvas.drawText("Amount Credit", pageWidth - 150, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setTextSize(13);
        canvas.drawText(customerName, leftMargin, topMargin(20), paint);
        canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(Double.valueOf(CommonUtils.removeSymbols(activity, mainTotal))), pageWidth - 150, topMargin(0), paint);

        if (pageSize.isPortrait()) {
            paint.setColor(activity.getResources().getColor(R.color.dark_gray));
            canvas.drawRect(leftMargin, topMargin(80), leftMargin + 50, pageHeight - bottomMargin(70), paint);
            canvas.drawRect(leftMargin + 200, topMargin(0), pageWidth - (leftMargin + 200), pageHeight - bottomMargin(0), paint);
            canvas.drawRect(pageWidth - (leftMargin + 100), topMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);

        } else {
            paint.setColor(activity.getResources().getColor(R.color.dark_gray));
            canvas.drawRect(leftMargin, topMargin(80), leftMargin + 50, pageHeight - bottomMargin(70), paint);
            canvas.drawRect(halfPageWidth - 100, topMargin(0), pageWidth - (leftMargin + 250), pageHeight - bottomMargin(0), paint);
            canvas.drawRect(pageWidth - (leftMargin + 100), topMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);
        }

        paint.setTextSize(8);
        paint.setTypeface(Typeface.create("BOLD", Typeface.BOLD));
        paint.setColor(activity.getResources().getColor(R.color.lightblue));

        canvas.drawLine(leftMargin, topMargin(-25), pageWidth - leftMargin, topMargin(0), paint);
        canvas.drawText("Sr.".toUpperCase(), leftMargin, topMargin(15), paint);
        canvas.drawText("Date".toUpperCase(), leftMargin + 50, topMargin(0), paint);

        if (pageSize.isPortrait()) {
            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " / " + activity.getResources().getString(R.string.deposit).toUpperCase(), leftMargin + 200, topMargin(0), paint);
            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + "(" + activity.getResources().getString(R.string.currency_india) + ")".toUpperCase(), pageWidth - (leftMargin + 200), topMargin(0), paint);
        } else {
            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + " / " + activity.getResources().getString(R.string.deposit).toUpperCase(), halfPageWidth - 100, topMargin(0), paint);
            canvas.drawText(activity.getResources().getString(R.string.billing).toUpperCase() + "(" + activity.getResources().getString(R.string.currency_india) + ")".toUpperCase(), pageWidth - (leftMargin + 250), topMargin(0), paint);
        }

        canvas.drawText(activity.getResources().getString(R.string.deposit).toUpperCase() + "(" + activity.getResources().getString(R.string.currency_india) + ")".toUpperCase(), pageWidth - (leftMargin + 100), topMargin(0), paint);
        canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);

        paint.setColor(activity.getResources().getColor(R.color.black));
        paint.setTextSize(11);

        for (int i = start; i >= end; i--) {
            JSONObject infoObject = infoArray.optJSONObject(i);
            String serialNo = (totalSize - i) + ".";

            if (i >= 0) {
                paint.setColor(activity.getResources().getColor(R.color.black));
                paint.setTextSize(11);
                paint.setTypeface(Typeface.create("BOLD", Typeface.BOLD));

                if (infoObject.has("billNo")) {
                    canvas.drawText(serialNo, leftMargin, topMargin(20), paint);
                    canvas.drawText(new SimpleDateFormat("MMMM dd, yyyy").format(CommonUtils.convertStringToDate(infoObject.optString("billDate"))), leftMargin + 50, topMargin(0), paint);

                    if (pageSize.isPortrait()) {
                        canvas.drawText(activity.getResources().getString(R.string.billing) + " No. " + infoObject.optString("billNo"), leftMargin + 200, topMargin(0), paint);
                        canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(infoObject.optDouble("billTotal")), pageWidth - (leftMargin + 200), topMargin(0), paint);

                    } else {
                        canvas.drawText(activity.getResources().getString(R.string.billing) + " No. " + infoObject.optString("billNo"), halfPageWidth - 100, topMargin(0), paint);
                        canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(infoObject.optDouble("billTotal")), pageWidth - (leftMargin + 250), topMargin(0), paint);

                    }
                } else {
                    canvas.drawText(serialNo, leftMargin, topMargin(20), paint);
                    canvas.drawText(new SimpleDateFormat("MMMM dd, yyyy").format(CommonUtils.convertStringToDate(infoObject.optString("depositDate"))), leftMargin + 50, topMargin(0), paint);

                    if (pageSize.isPortrait())
                        canvas.drawText(activity.getResources().getString(R.string.deposit) + " No. " + infoObject.optString("depositNo"), leftMargin + 200, topMargin(0), paint);
                    else
                        canvas.drawText(activity.getResources().getString(R.string.deposit) + " No. " + infoObject.optString("depositNo"), halfPageWidth - 100, topMargin(0), paint);

                    canvas.drawText(activity.getResources().getString(R.string.currency_india) + " " + CommonUtils.getDecimal(infoObject.optDouble("depositAmount")), pageWidth - (leftMargin + 100), topMargin(0), paint);
                }

                if (i > end && i != 0) {
                    paint.setColor(activity.getResources().getColor(R.color.lightblue));
                    canvas.drawLine(leftMargin, topMargin(10), pageWidth - leftMargin, topMargin(0), paint);
                }

            } else
                break;
        }

        paint.setColor(activity.getResources().getColor(R.color.lightblue));
        paint.setTextSize(13);
        canvas.drawLine(leftMargin, pageHeight - bottomMargin(0), pageWidth - leftMargin, pageHeight - bottomMargin(0), paint);
        canvas.drawText("Authorized Signatory".toUpperCase(), leftMargin, pageHeight - bottomMargin(-20), paint);
    }

    private int topMargin(int addMargin) {
        topMarginAdd = topMarginAdd + addMargin;
        return topMarginAdd;
    }

    private int bottomMargin(int addMargin) {
        bottomMarginAdd = bottomMarginAdd + addMargin;
        return bottomMarginAdd;
    }
}