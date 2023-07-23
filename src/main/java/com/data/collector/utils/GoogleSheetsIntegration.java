package com.data.collector.utils;

import com.data.collector.clients.GoogleSheetsClient;
import com.data.collector.dto.FormRequestDTO;
import com.data.collector.dto.QuestionAnswerDTO;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsIntegration {

    // Replace SPREADSHEET_ID with your Google Sheets spreadsheet ID
    private static final String SPREADSHEET_ID = "your_spreadsheet_id";

    public static void exportToGoogleSheets(FormRequestDTO formResponse) {
        try {
            // Get the authenticated credentials
            Credential credentials = GoogleSheetsClient.getCredentials();
            Sheets sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GoogleSheetsClient.JSON_FACTORY, credentials)
                    .setApplicationName(GoogleSheetsClient.APPLICATION_NAME)
                    .build();

            // Prepare the data to be written to the spreadsheet
            List<List<Object>> data = new ArrayList<>();
            List<Object> headerRow = new ArrayList<>();
            headerRow.add("Form ID");
            for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
                String questionId = questionAnswer.getQuestionId();
                if (!headerRow.contains(questionId)) {
                    headerRow.add(questionId); // Add unique question IDs to the header row
                }
            }
            data.add(headerRow);

            // Populate the data row with form response answers
            List<Object> rowData = new ArrayList<>();
            rowData.add(formResponse.getFormId());
            for (Object questionId : headerRow.subList(1, headerRow.size())) {
                String answer = getAnswerForQuestion(formResponse, questionId.toString());
                rowData.add(answer != null ? answer : ""); // Add the answer to the data row
            }
            data.add(rowData);

            // Set the range where you want to append the data in the spreadsheet (e.g., Sheet1!A2)
            String range = "Sheet1!A2";

            // Write the form response data to the spreadsheet
            ValueRange appendBody = new ValueRange().setValues(data);
            sheetsService.spreadsheets().values()
                    .append(SPREADSHEET_ID, range, appendBody)
                    .setValueInputOption("RAW")
                    .execute();

            // Create the bar chart using the header row and the data range
            String chartRange = "Sheet1!A1:" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(headerRow.size() - 1) + (data.size() + 1);
            createBarChart(chartRange);


            System.out.println("Form response data exported to Google Sheets successfully!");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            System.err.println("Error exporting form response data to Google Sheets.");
        }
    }

    private static String getAnswerForQuestion(FormRequestDTO formResponse, String questionId) {
        for (QuestionAnswerDTO questionAnswer : formResponse.getQuestionAnswers()) {
            if (questionAnswer.getQuestionId().equals(questionId)) {
                return questionAnswer.getAnswer();
            }
        }
        return null;
    }

    private static void createBarChart(String chartRange) {
        try {
            // Get the authenticated credentials
            Credential credentials = GoogleSheetsClient.getCredentials();
            Sheets sheetsService = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GoogleSheetsClient.JSON_FACTORY, credentials)
                    .setApplicationName(GoogleSheetsClient.APPLICATION_NAME)
                    .build();

            // Create the chart data source with the provided data range
            ChartSourceRange sourceRange = new ChartSourceRange().setSources(Collections.singletonList(new GridRange()
                    .setSheetId(0)
                    .setStartRowIndex(1) // Adjust the start row index based on your data, 0 for header
                    .setEndRowIndex(100) // Adjust the end row index based on your data
                    .setStartColumnIndex(1) // Adjust the start column index based on your data, 0 for Form ID
                    .setEndColumnIndex(6) // Adjust the end column index based on your data
            ));


            // Create the embedded chart with the specified title, chart type, and data source
            EmbeddedChart chart = new EmbeddedChart()
                    .setSpec(new ChartSpec().setTitle("Form Responses Bar Chart")
                            .setBasicChart(new BasicChartSpec().set("BAR", new ChartData().setSourceRange(sourceRange))));


            // Add the chart to the Google Sheets
            AddChartRequest addChartRequest = new AddChartRequest()
                    .setChart(chart)
                    .set("position", new EmbeddedObjectPosition().setSheetId(0).setOverlayPosition(new OverlayPosition().setAnchorCell(new GridCoordinate().setSheetId(0).setRowIndex(4).setColumnIndex(4))));

            Request request = new Request().setAddChart(addChartRequest);
            BatchUpdateSpreadsheetRequest batchUpdateRequest = new BatchUpdateSpreadsheetRequest().setRequests(Collections.singletonList(request));
            sheetsService.spreadsheets().batchUpdate(SPREADSHEET_ID, batchUpdateRequest).execute();


            System.out.println("Bar chart created successfully!");
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            System.err.println("Error creating bar chart in Google Sheets.");
        }
    }

}
