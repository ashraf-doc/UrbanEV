/*
File originally created, published and licensed by contributors of the org.matsim.* project.
Please consider the original license notice below.
This is a modified version of the original source code!

Modified 2020 by Lennart Adenaw, Technical University Munich, Chair of Automotive Technology
email	:	lennart.adenaw@tum.de
*/

/* ORIGINAL LICENSE

 * *********************************************************************** *
 * project: org.matsim.*
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2019 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** *
 */

package de.tum.mw.ftm.matsim.contrib.urban_ev.fleet;

import org.matsim.contrib.ev.EvUnits;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.core.utils.io.MatsimXmlWriter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class ElectricFleetWriter extends MatsimXmlWriter {
	private final Stream<? extends ElectricVehicleSpecification> vehicleSpecifications;

	public ElectricFleetWriter(Stream<? extends ElectricVehicleSpecification> vehicleSpecifications) {
		this.vehicleSpecifications = vehicleSpecifications;
	}

	public void write(String file) {
		openFile(file);
		writeDoctype("vehicles", "http://matsim.org/files/dtd/electric_vehicles_v1.dtd");
		writeStartTag("vehicles", Collections.emptyList());
		writeVehicles();
		writeEndTag("vehicles");
		close();
	}

	private void writeVehicles() {
		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		format.setMinimumIntegerDigits(1);
		format.setMaximumFractionDigits(2);//XXX this is asymmetric to reading vehicles
		format.setGroupingUsed(false);

		vehicleSpecifications.forEach(v -> {
			List<Tuple<String, String>> atts = Arrays.asList(Tuple.of("id", v.getId().toString()),
					Tuple.of("battery_capacity", format.format(EvUnits.J_to_kWh(v.getBatteryCapacity())) + ""),
					//TODO consider renaming to initial_charge -- SOC suggest [%] not [kWh]
					Tuple.of("initial_soc", format.format(EvUnits.J_to_kWh(v.getInitialSoc())) + ""),
					Tuple.of("vehicle_type", v.getVehicleType().getName()),
					Tuple.of("charger_types", String.join(",", v.getChargerTypes())));
			writeStartTag("vehicle", atts, true);
		});
	}
}
