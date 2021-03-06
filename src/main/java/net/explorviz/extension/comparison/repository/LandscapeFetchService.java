package net.explorviz.extension.comparison.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.extension.comparison.resources.LandscapeResourceComparing;
import net.explorviz.model.application.Application;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.server.main.Configuration;

/**
 * Service for fetching landscapes (merged and replay) that is used by
 * {@link LandscapeResourceComparing}.
 *
 * @author josw
 *
 */
public class LandscapeFetchService {

	static final Logger LOGGER = LoggerFactory.getLogger(LandscapeFetchService.class.getName());
	private static LandscapeFetchService instance;

	private final ExtensionAPIImpl extensionApi = ExtensionAPIImpl.getInstance();
	private final Merger appMerger = new Merger();

	public static synchronized LandscapeFetchService getInstance() {
		if (LandscapeFetchService.instance == null) {
			LandscapeFetchService.instance = new LandscapeFetchService();
		}
		return LandscapeFetchService.instance;
	}

	public Landscape fetchLandscapeForComparison(final long timestamp) {
		return extensionApi.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
	}

	/**
	 * Takes two {@link Landscape}s and builds one merged {@link Landscape}, i.e.
	 * {@link Landscape} with merged {@link Application}s.
	 * 
	 * @param firstLandscape
	 * @param secondLandscape
	 * @return merged {@link Landscape}, i.e. {@link Landscape} with merged
	 *         {@link Application}s
	 */
	public Landscape fetchMergedLandscape(final Landscape firstLandscape, final Landscape secondLandscape) {
		final Landscape mergedLandscape = secondLandscape;
		Application mergedApp = null;

		for (final net.explorviz.model.landscape.System sys : mergedLandscape.getSystems()) {
			for (final NodeGroup nodegroup : sys.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application app2 : node.getApplications()) {
						final String app2Name = app2.getName();

						final Application app1 = appContained(firstLandscape, app2Name);
						if (app1 == null) {
							LOGGER.error(
									"You can not compare two complete different applications. The application {} is not contained in the other landscape.",
									app2Name);
						} else {
							mergedApp = appMerger.appMerge(app1, app2);
						}
						node.getApplications().remove(app2);
						node.getApplications().add(mergedApp);
					}

				}

			}
		}
		return mergedLandscape;
	}

	/**
	 * Searches {@link Application} with fully qualified name (appName).
	 *
	 * @param landscape
	 * @param appName
	 * @return if {@link Application} with name exists: return it, else: null
	 */
	private Application appContained(final Landscape landscape, final String appName) {
		Application app = new Application();

		for (final net.explorviz.model.landscape.System sys : landscape.getSystems()) {
			for (final NodeGroup nodegroup : sys.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application checkApp : node.getApplications()) {
						if (checkApp.getName().equals(appName)) {
							app = checkApp;
						} else {
							app = null;
						}
					}
				}

			}
		}

		return app;
	}

}
