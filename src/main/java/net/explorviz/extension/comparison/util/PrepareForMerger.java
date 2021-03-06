package net.explorviz.extension.comparison.util;

import java.util.List;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.CumulatedClazzCommunication;

/**
 * Extends all elements of an {@link Application} by a {@link Status} and
 * {@link Clazz}es further by an instance count that holds the difference of the
 * instance counts between the clazz in the two versions. The {@link Status} is
 * necessary to merge two {@link Application}s.
 *
 * @author jweg
 *
 */
public final class PrepareForMerger {

	public static final String STATUS = "status";
	public static final Status DEFAULT_STATUS = Status.ORIGINAL;
	public static final String DIFF_INSTANCE_COUNT = "diffInstanceCount";
	public static final Integer DEFAULT_DIFF_INSTANCE_COUNT = 0;

	// because this is a utility class
	private PrepareForMerger() {
	}

	/**
	 * Adds a {@link Status} attribute to every element in the {@link Application}.
	 * This serves as preparation for merging two {@link Application}s.
	 *
	 * @param mergingApp
	 *            {@link Application} that should be merged without status attribute
	 * @return {@link Application} that should be merged with status attribute
	 */
	public static Application addStatusToApp(final Application mergingApp) {

		for (final CumulatedClazzCommunication cumulatedCommunication : mergingApp.getCumulatedClazzCommunications()) {
			cumulatedCommunication.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);
			for (final AggregatedClazzCommunication aggregatedCommunication : cumulatedCommunication
					.getAggregatedClazzCommunications()) {
				aggregatedCommunication.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);
				for (final ClazzCommunication communication : aggregatedCommunication
						.getOutgoingClazzCommunications()) {
					communication.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);
				}
			}
		}

		for (final Component component : mergingApp.getComponents()) {
			component.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);

			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);
				clazz.getExtensionAttributes().put(DIFF_INSTANCE_COUNT, DEFAULT_DIFF_INSTANCE_COUNT);
			}

			addStatusToChildren(component.getChildren());
		}

		return mergingApp;
	}

	/**
	 * Helps the {@link #addStatusToApp(Application)} method to add the status
	 * recursively to all children of {@link Component}s and their {@link Clazz}es.
	 *
	 * @param components
	 *            list of child components
	 */
	private static void addStatusToChildren(final List<Component> components) {
		for (final Component component : components) {
			component.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);

			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(STATUS, DEFAULT_STATUS);
				clazz.getExtensionAttributes().put(DIFF_INSTANCE_COUNT, DEFAULT_DIFF_INSTANCE_COUNT);
			}

			addStatusToChildren(component.getChildren());

		}
	}
}
