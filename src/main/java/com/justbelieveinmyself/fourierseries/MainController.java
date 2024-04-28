package com.justbelieveinmyself.fourierseries;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.util.function.Function;

public class MainController {

    @FXML
    private ChoiceBox<String> functionChoiceBox;

    @FXML
    private TextField pointCountTextField;

    @FXML
    private TextField lowerLimitTextField;

    @FXML
    private TextField upperLimitTextField;
    @FXML
    private TextField termsCountTextField;

    @FXML
    private LineChart lineChart;

    @FXML
    private void initialize() {
        // Инициализация выпадающего списка функций
        functionChoiceBox.getItems().addAll("прямоугольный импульс", "пилообразная волна", "прямоугольная волна", "треугольная волна");
        functionChoiceBox.setValue("прямоугольный импульс"); // Значение по умолчанию


        // Установка ограничений для ввода чисел в текстовые поля
        pointCountTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                pointCountTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        lowerLimitTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]*")) {
                lowerLimitTextField.setText(newValue.replaceAll("[^\\d-]", ""));
            }
        });
        upperLimitTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[-\\d]*")) {
                upperLimitTextField.setText(newValue.replaceAll("[^\\d-]", ""));
            }
        });
    }

    @FXML
    private void buildGraph() {
        lineChart.getData().clear();
        // Получение выбранных пользователем параметров и построение графика
        String selectedFunction = functionChoiceBox.getValue();
        int pointCount = Integer.parseInt(pointCountTextField.getText());
        double lowerLimit = Double.parseDouble(lowerLimitTextField.getText());
        double upperLimit = Double.parseDouble(upperLimitTextField.getText());
        int termsCount = Integer.parseInt(termsCountTextField.getText());

        // Создание серии данных для графика функции и для графика ряда Фурье
        XYChart.Series<Double, Double> functionSeries = new XYChart.Series<>();
        functionSeries.setName(selectedFunction);
        XYChart.Series<Double, Double> fourierSeries = new XYChart.Series<>();
        fourierSeries.setName("Ряд Фурье");

        // Вычисление значений функции и добавление их в серию данных
        double step = (upperLimit - lowerLimit) / (pointCount - 1);
        for (double x = lowerLimit; x <= upperLimit; x += step) {
            double y = evaluateFunction(selectedFunction, x);
            functionSeries.getData().add(new XYChart.Data<>(x, y));

            // Добавление значений ряда Фурье
            double fourierY = 0;
            for (int n = 1; n <= termsCount; n++) {
                fourierY += calculateFourierCoefficient(z -> evaluateFunction(selectedFunction, z), n) * Math.cos(n * x);
            }
            fourierSeries.getData().add(new XYChart.Data<>(x, fourierY));
        }

        // Добавление серий данных на график
        lineChart.getData().addAll(functionSeries, fourierSeries);
    }


    private double evaluateFunction(String function, double x) {
        switch (function) {
            case "прямоугольный импульс":
                return x % 2 >= 1 ? 1 : 0; // Пример прямоугольного импульса с периодом 2
            case "пилообразная волна":
                return x % 1; // Пример пилообразной волны с периодом 1
            case "прямоугольная волна":
                return x % 2 < 1 ? 1 : -1; // Пример прямоугольной волны с периодом 2
            case "треугольная волна":
                return 1 - Math.abs(x % 2 - 1); // Пример треугольной волны с периодом 2
            default:
                return 0.0;
        }
    }

    private double integrate(Function<Double, Double> function, double lowerBound, double upperBound) {
        int n = 10000; // количество разбиений
        double h = (upperBound - lowerBound) / n;
        double integral = 0.0;
        for (int i = 0; i < n; i++) {
            double x1 = lowerBound + i * h;
            double x2 = lowerBound + (i + 1) * h;
            integral += function.apply((x1 + x2) / 2) * h; // прямоугольный метод
        }
        return integral;
    }



    private double calculateFourierCoefficient(Function<Double, Double> function, int n) {
        // Вычисление коэффициента ряда Фурье с помощью численного интегрирования
        return integrate(x -> function.apply(x) * Math.cos(n * x), -Math.PI, Math.PI) / Math.PI +
                integrate(x -> function.apply(x) * Math.sin(n * x), -Math.PI, Math.PI) / Math.PI;
    }

}
