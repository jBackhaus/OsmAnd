package net.osmand.plus.mapcontextmenu.controllers;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.osmand.data.PointDescription;
import net.osmand.plus.MapMarkersHelper;
import net.osmand.plus.MapMarkersHelper.MapMarker;
import net.osmand.plus.settings.backend.OsmandPreference;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.helpers.MapMarkerDialogHelper;
import net.osmand.plus.mapcontextmenu.MenuBuilder;
import net.osmand.plus.mapcontextmenu.MenuController;
import net.osmand.util.Algorithms;

public class MapMarkerMenuController extends MenuController {

	private MapMarker mapMarker;

	public MapMarkerMenuController(@NonNull MapActivity mapActivity, @NonNull PointDescription pointDescription, @NonNull MapMarker mapMarker) {
		super(new MenuBuilder(mapActivity), pointDescription, mapActivity);
		final boolean useStateList = Build.VERSION.SDK_INT >= 21;
		this.mapMarker = mapMarker;
		builder.setShowNearestWiki(true);

		leftTitleButtonController = new TitleButtonController() {
			@Override
			public void buttonPressed() {
				MapActivity activity = getMapActivity();
				if (activity != null) {
					MapMarkersHelper markersHelper = activity.getMyApplication().getMapMarkersHelper();
					MapMarker marker = getMapMarker();
					if (marker.history) {
						markersHelper.restoreMarkerFromHistory(marker, 0);
					} else {
						markersHelper.moveMapMarkerToHistory(marker);
					}
					activity.getContextMenu().close();
				}
			}
		};
		leftTitleButtonController.caption = mapActivity.getString(mapMarker.history ? R.string.shared_string_restore : R.string.mark_passed);
		leftTitleButtonController.startIcon = createPassedIcon(getPassedIconBgNormalColorId());

		if (!mapMarker.history) {
			rightTitleButtonController = new TitleButtonController() {
				@Override
				public void buttonPressed() {
					MapActivity activity = getMapActivity();
					if (activity != null) {
						OsmandPreference<Boolean> indication
								= activity.getMyApplication().getSettings().MARKERS_DISTANCE_INDICATION_ENABLED;
						if (!indication.get()) {
							indication.set(true);
							activity.getMapLayers().getMapWidgetRegistry().updateMapMarkersMode(activity);
						}
						MapMarkersHelper markersHelper = activity.getMyApplication().getMapMarkersHelper();
						markersHelper.moveMarkerToTop(getMapMarker());
						activity.getContextMenu().close();
					}
				}
			};
			rightTitleButtonController.caption = mapActivity.getString(R.string.make_active);
			rightTitleButtonController.startIcon = createShowOnTopbarIcon(getDeviceTopNormalColorId());
		}
	}

	private int getPassedIconBgNormalColorId() {
		return isLight() ? R.color.active_color_primary_light : R.color.active_color_primary_dark;
	}

	@Nullable
	private LayerDrawable createPassedIcon(int bgColorRes) {
		MapActivity mapActivity = getMapActivity();
		if (mapActivity != null) {
			ShapeDrawable bg = new ShapeDrawable(new OvalShape());
			bg.getPaint().setColor(ContextCompat.getColor(mapActivity, bgColorRes));
			Drawable ic = getIcon(R.drawable.ic_action_marker_passed, 0);
			return new LayerDrawable(new Drawable[]{bg, ic});
		} else {
			return null;
		}
	}

	private int getDeviceTopNormalColorId() {
		return isLight() ? R.color.icon_color_default_light : R.color.icon_color_default_dark;
	}

	private LayerDrawable createShowOnTopbarIcon(int bgColorRes) {
		Drawable background = getIcon(R.drawable.ic_action_device_top, bgColorRes);
		Drawable topbar = getIcon(R.drawable.ic_action_device_topbar, R.color.active_color_primary_light);
		return new LayerDrawable(new Drawable[]{background, topbar});
	}

	@Override
	protected void setObject(Object object) {
		if (object instanceof MapMarker) {
			this.mapMarker = (MapMarker) object;
		}
	}

	@Override
	protected Object getObject() {
		return mapMarker;
	}

	public MapMarker getMapMarker() {
		return mapMarker;
	}

	@Override
	public boolean needTypeStr() {
		return !Algorithms.isEmpty(getNameStr());
	}

	@Override
	public boolean displayDistanceDirection() {
		return true;
	}

	@Override
	public Drawable getRightIcon() {
		MapActivity mapActivity = getMapActivity();
		if (mapActivity != null) {
			return MapMarkerDialogHelper.getMapMarkerIcon(mapActivity.getMyApplication(), mapMarker.colorIndex);
		} else {
			return null;
		}
	}

	@NonNull
	@Override
	public String getTypeStr() {
		MapActivity mapActivity = getMapActivity();
		if (mapActivity != null) {
			return mapMarker.getPointDescription(mapActivity).getTypeName();
		} else {
			return "";
		}
	}

	@Override
	public boolean needStreetName() {
		return !needTypeStr();
	}

	@Override
	public int getWaypointActionIconId() {
		return R.drawable.ic_action_edit_dark;
	}

	@Override
	public int getWaypointActionStringId() {
		return R.string.shared_string_edit;
	}
}