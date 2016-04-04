package com.github.changamire;

import java.util.List;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.Credits;
import com.vaadin.addon.charts.model.DashStyle;
import com.vaadin.addon.charts.model.DataSeries;
import com.vaadin.addon.charts.model.DataSeriesItem;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.Marker;
import com.vaadin.addon.charts.model.PlotOptionsLine;
import com.vaadin.addon.charts.model.Title;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.Color;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class SparklineChart extends VerticalLayout {

    public SparklineChart(final String name,
            final Color color, final List<Float> dataPoints,
            final int average) {

        Label title = new Label(name);
        title.addStyleName(ValoTheme.LABEL_SMALL);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        addComponent(title);

        addComponent(buildSparkline(dataPoints, color));

        Label highLow = new Label("High <b>" + java.util.Collections.max(dataPoints)
                + "</b> &nbsp;&nbsp;&nbsp; Low <b>"
                + java.util.Collections.min(dataPoints) + "</b>", ContentMode.HTML);
        highLow.addStyleName(ValoTheme.LABEL_TINY);
        highLow.addStyleName(ValoTheme.LABEL_LIGHT);
        addComponent(highLow);
    }

    private Component buildSparkline(final List<Float> values, final Color color) {
        Chart spark = new Chart();
        spark.getConfiguration().setTitle("");
        spark.getConfiguration().getChart().setType(ChartType.LINE);
        spark.getConfiguration().getChart().setAnimation(false);
        spark.setWidth("180px");
        spark.setHeight("70px");
        spark.setImmediate(true);

        DataSeries series = new DataSeries();
        for (Float value : values) {
            DataSeriesItem item = new DataSeriesItem("", value.intValue());
            series.add(item);
        }

        spark.getConfiguration().setSeries(series);
        spark.getConfiguration().getTooltip().setEnabled(false);

        Configuration conf = series.getConfiguration();
        Legend legend = new Legend();
        legend.setEnabled(false);
        conf.setLegend(legend);

        Credits c = new Credits("");
        spark.getConfiguration().setCredits(c);

        PlotOptionsLine opts = new PlotOptionsLine();
        opts.setTurboThreshold(25000);
        opts.setAllowPointSelect(false);
        opts.setColor(color);
        opts.setDataLabels(new Labels(false));
        opts.setLineWidth(1);
        opts.setShadow(false);
        opts.setDashStyle(DashStyle.SOLID);
        opts.setMarker(new Marker(false));
        opts.setEnableMouseTracking(false);
        opts.setAnimation(false);
        spark.getConfiguration().setPlotOptions(opts);

        XAxis xAxis = spark.getConfiguration().getxAxis();
        YAxis yAxis = spark.getConfiguration().getyAxis();

        SolidColor transparent = new SolidColor(0, 0, 0, 0);

        xAxis.setLabels(new Labels(false));
        xAxis.setTickWidth(0);
        xAxis.setLineWidth(0);

        yAxis.setTitle(new Title(""));
        yAxis.setAlternateGridColor(transparent);
        yAxis.setLabels(new Labels(false));
        yAxis.setLineWidth(0);
        yAxis.setGridLineWidth(0);

        spark.drawChart();
        return spark;
    }
}
