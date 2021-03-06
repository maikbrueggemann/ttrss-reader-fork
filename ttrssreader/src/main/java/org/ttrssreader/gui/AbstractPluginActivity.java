/*
 * ttrss-reader-fork for Android
 *
 * Copyright (C) 2010 Nils Braden
 * Copyright 2013 two forty four a.m. LLC <http://www.twofortyfouram.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <http://www.apache.org/licenses/LICENSE-2.0>
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.ttrssreader.gui;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.twofortyfouram.locale.api.R;

import org.ttrssreader.controllers.Controller;

/**
 * Superclass for plug-in Activities. This class takes care of initializing aspects of the plug-in's UI to
 * look more integrated with the plug-in host.
 */
public abstract class AbstractPluginActivity extends Activity {

	private static final String TAG = AbstractPluginActivity.class.getSimpleName();

	/**
	 * Flag boolean that can only be set to true via the "Don't Save"
	 * twofortyfouram_locale_menu_dontsave menu item in
	 * {@link #onMenuItemSelected(int, MenuItem)}.
	 */
	/*
	 * There is no need to save/restore this field's state.
	 */
	private boolean mIsCancelled = false;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		setTheme(Controller.getInstance().getTheme());
		super.onCreate(savedInstanceState);
		CharSequence callingApplicationLabel = null;
		try {
			callingApplicationLabel = getPackageManager()
					.getApplicationLabel(getPackageManager().getApplicationInfo(getCallingPackage(), 0));
		} catch (final NameNotFoundException e) {
			Log.e(TAG, "Calling package couldn't be found", e); //$NON-NLS-1$
		}
		if (null != callingApplicationLabel) {
			setTitle(callingApplicationLabel);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);
		if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

		/*
		 * Note: There is a small TOCTOU error here, in that the host could be uninstalled right after
		 * launching the plug-in. That would cause getApplicationIcon() to return the default application
		 * icon. It won't fail, but it will return an incorrect icon.
		 *
		 * In practice, the chances that the host will be uninstalled while the plug-in UI is running are very
		 * slim.
		 */
		try {
			if (getActionBar() != null)
				getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
		} catch (final NameNotFoundException e) {
			Log.w(TAG, "An error occurred loading the host's icon", e); //$NON-NLS-1$
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		final int id = item.getItemId();

		if (android.R.id.home == id) {
			finish();
			return true;
		} else if (R.id.twofortyfouram_locale_menu_dontsave == id) {
			mIsCancelled = true;
			finish();
			return true;
		} else if (R.id.twofortyfouram_locale_menu_save == id) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * During {@link #finish()}, subclasses can call this method to determine whether the Activity was
	 * canceled.
	 *
	 * @return True if the Activity was canceled. False if the Activity was not canceled.
	 */
	protected boolean isCanceled() {
		return mIsCancelled;
	}
}
