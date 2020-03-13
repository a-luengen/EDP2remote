package org.palladiosimulator.edp2.repository.remote.server.service;

import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Quantity;
import javax.measure.unit.SI;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.edp2.dao.MeasurementsDao;
import org.palladiosimulator.edp2.dao.MeasurementsDaoFactory;
import org.palladiosimulator.edp2.dao.impl.MeasurementsDaoFactoryImpl;
import org.palladiosimulator.edp2.models.ExperimentData.DataSeries;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentDataFactory;
import org.palladiosimulator.edp2.models.ExperimentData.ExperimentRun;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.edp2.models.ExperimentData.MeasurementRange;
import org.palladiosimulator.edp2.models.ExperimentData.RawMeasurements;
import org.palladiosimulator.edp2.models.measuringpoint.MeasuringpointFactory;
import org.palladiosimulator.edp2.util.MeasurementsUtility;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.TupleMeasurement;
import org.palladiosimulator.metricspec.Identifier;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.metricspec.MetricSetDescription;
import org.springframework.stereotype.Component;

@Component
public class DataseriesService {

	private static final MeasuringpointFactory MEASURING_POINT_FACTORY = MeasuringpointFactory.eINSTANCE;
	private static final ExperimentDataFactory EXPERIMENT_DATA_FACTORY = ExperimentDataFactory.eINSTANCE;
	
	public EList<DataSeries> getDefaultDataSeries(ExperimentRun run) {
		run.getMeasurement().get(0)
			.getMeasurementRanges().get(0).getRawMeasurements().getDataSeries();
		
		return null;
	}

	public void addLongDataSeriesToMeasurement(Measurement measurement, long[] values) {
		MeasuringValue mValue;
		for (long val : values) {
			mValue = new TupleMeasurement(
					(MetricSetDescription) measurement.getMeasuringType().getMetric(),
					Measure.valueOf(val, SI.MILLI(SI.SECOND))
					);
			MeasurementsUtility.storeMeasurement(measurement, mValue);
		}
	}	
}
