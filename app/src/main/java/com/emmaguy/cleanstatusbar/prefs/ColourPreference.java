package com.emmaguy.cleanstatusbar.prefs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.emmaguy.cleanstatusbar.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;

// Adapted from DashClock: https://code.google.com/p/dashclock/source/browse/main/src/main/java/com/google/android/apps/dashclock/configuration/ColorPreference.java
public class ColourPreference extends Preference {
    private static ArrayList<Colour> mColours = new ArrayList<Colour>();

    public ColourPreference(Context context) {
        this(context, null);
    }

    public ColourPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.colour_preference);
    }

    private String getUserColoursKey() {
        return getKey() + "colours";
    }

    @Override
    protected void onClick() {
        super.onClick();

        ColourDialogFragment fragment = ColourDialogFragment.newInstance();
        fragment.setPreference(this);

        Activity activity = (Activity) getContext();
        activity.getFragmentManager().beginTransaction()
                .add(fragment, getFragmentTag())
                .commit();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        String json = getSharedPreferences().getString(getUserColoursKey(), "");
        mColours = new Gson().fromJson(json, new TypeToken<ArrayList<Colour>>() {}.getType());

        if (mColours == null) {
            mColours = new ArrayList<Colour>();

            String[] defaultColourNames = getContext().getResources().getStringArray(R.array.default_colour_choices);
            String[] defaultColourValues = getContext().getResources().getStringArray(R.array.default_colour_choice_values);
            for (int i = 0; i < defaultColourValues.length; i++) {
                mColours.add(new Colour(defaultColourNames[i], Color.parseColor(defaultColourValues[i])));
            }
        }
        Collections.sort(mColours);

        int value = getPersistedInt(0);
        if (value == 0) {
            value = mColours.get(0).mColourValue;
            setValue(value);
        }

        setColourValue((ImageView) view.findViewById(R.id.colour_view), value);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            persistInt(value);
            notifyChanged();
        }
    }

    public String getFragmentTag() {
        return "colour_" + getKey();
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();

        Activity activity = (Activity) getContext();
        ColourDialogFragment fragment = (ColourDialogFragment) activity.getFragmentManager().findFragmentByTag(getFragmentTag());
        if (fragment != null) {
            // re-bind preference to fragment
            fragment.setPreference(this);
        }
    }

    public static class ColourDialogFragment extends DialogFragment implements View.OnClickListener {
        private ColourPreference mPreference;
        private ColorPreferenceListAdapter mAdapter;
        private ListView mListView;
        private AlertDialog mAlertDialog;

        public ColourDialogFragment() {
        }

        public static ColourDialogFragment newInstance() {
            return new ColourDialogFragment();
        }

        public void setPreference(ColourPreference preference) {
            mPreference = preference;

            tryBindLists();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            tryBindLists();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            mListView = new ListView(getActivity());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> listView, View view, int position, long itemId) {
                    Colour item = mAdapter.getItem(position);
                    mPreference.setValue(item.mColourValue);
                    dismiss();
                }
            });
            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.title_delete_colour)
                            .setMessage(R.string.message_delete_colour)
                            .setNegativeButton(R.string.no, null)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    mColours.remove(position);
                                    ((BaseAdapter) ((HeaderViewListAdapter) mListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                                    mPreference.getSharedPreferences().edit().putString(mPreference.getUserColoursKey(), new Gson().toJson(mColours)).apply();
                                }
                            })
                            .show();
                    return true;
                }
            });
            View footerView = LayoutInflater.from(getActivity()).inflate(R.layout.colour_preference_footer, null);
            footerView.setOnClickListener(this);
            mListView.addFooterView(footerView);

            tryBindLists();

            return new AlertDialog.Builder(getActivity())
                    .setView(mListView)
                    .create();
        }

        private void tryBindLists() {
            if (mPreference == null) {
                return;
            }

            if (isAdded() && mAdapter == null) {
                mAdapter = new ColorPreferenceListAdapter(getActivity());
            }

            if (mAdapter != null && mListView != null) {
                mListView.setAdapter(mAdapter);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.footer) {
                showAddColourDialog();
            }
        }

        /**
         * @param str
         * @return true if the colour is valid, false otherwise
         */
        private static boolean isValidColor(String str) {
            try {
                Color.parseColor(str);
                return true;
            } catch (Exception e1) {
                try {
                    Color.parseColor("#" + str);
                    return true;
                } catch (Exception e2) {
                }
            }
            return false;
        }

        /**
         * @param str
         * @return the corresponding colour, or 0 if the colour isn't valid
         */
        private static int getColor(String str) {
            try {
                return Color.parseColor(str);
            } catch (Exception e1) {
                try {
                    return Color.parseColor("#" + str);
                } catch (Exception e2) {
                }
            }
            return 0;
        }

        private void showAddColourDialog() {
            View root = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_colour, null);

            final EditText editName = (EditText) root.findViewById(R.id.edit_color_name);
            final EditText editValue = (EditText) root.findViewById(R.id.edit_color_value);
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    updateAddButtonEnabledState();
                }

                private void updateAddButtonEnabledState() {
                    boolean enabled = isValidColor(editValue.getText().toString()) && !TextUtils.isEmpty(editName.getText());
                    mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(enabled);
                }
            };
            editName.addTextChangedListener(textWatcher);
            editValue.addTextChangedListener(textWatcher);

            mAlertDialog = new AlertDialog.Builder(getActivity())
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAlertDialog = null;
                        }
                    })
                    .setPositiveButton(R.string.button_add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final String colorName = editName.getText().toString();
                            final String colourValue = editValue.getText().toString();

                            int colour = getColor(colourValue);
                            mColours.add(new Colour(colorName, colour));
                            Collections.sort(mColours);
                            ((BaseAdapter) ((HeaderViewListAdapter) mListView.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                            mPreference.getSharedPreferences().edit().putString(mPreference.getUserColoursKey(), new Gson().toJson(mColours)).apply();
                            hideKeyboard(getActivity(), editValue);
                            mAlertDialog = null;
                        }
                    })
                    .setTitle(R.string.title_add_new_colour)
                    .setView(root)
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            mAlertDialog = null;
                        }
                    })
                    .create();
            mAlertDialog.show();
            mAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        }
    }

    protected static void hideKeyboard(Context c, View view) {
        InputMethodManager in = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    static class ColorPreferenceListAdapter extends BaseAdapter {
        private final Resources mResources;
        private final LayoutInflater mLayoutInflater;

        public ColorPreferenceListAdapter(Context context) {
            mLayoutInflater = LayoutInflater.from(context);
            mResources = context.getResources();
        }

        @Override
        public int getCount() {
            return mColours.size();
        }

        @Override
        public Colour getItem(int position) {
            return mColours.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                v = mLayoutInflater.inflate(R.layout.colour_preference_row, null);
                v.setPadding(dpToPx(15), dpToPx(10), dpToPx(15), dpToPx(10));

                holder = new ViewHolder();
                holder.colourName = (TextView) v.findViewById(R.id.colour_name);
                holder.colour = (ImageView) v.findViewById(R.id.colour_view);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            Colour colour = getItem(position);
            holder.colourName.setText(colour.mColourName);
            setColourValue(holder.colour, colour.mColourValue);

            return v;
        }

        private int dpToPx(float dp) {
            return (int) (dp * mResources.getDisplayMetrics().density);
        }

        class ViewHolder {
            public TextView colourName;
            public ImageView colour;
        }
    }

    private static void setColourValue(ImageView imageView, int colour) {
        Drawable currentDrawable = imageView.getDrawable();
        GradientDrawable colorChoiceDrawable;
        if (currentDrawable != null && currentDrawable instanceof GradientDrawable) {
            // reuse drawable
            colorChoiceDrawable = (GradientDrawable) currentDrawable;
        } else {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(GradientDrawable.OVAL);
        }

        // Set stroke to darker version of color
        float scale = 0.80f;
        int stroke = Color.rgb((int) (Color.red(colour) * scale), (int) (Color.green(colour) * scale), (int) (Color.blue(colour) * scale));

        colorChoiceDrawable.setColor(colour);
        colorChoiceDrawable.setStroke((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, imageView.getResources().getDisplayMetrics()), stroke);
        imageView.setImageDrawable(colorChoiceDrawable);
    }

    static class Colour implements Comparable<Colour> {
        int mColourValue;
        String mColourName;

        public Colour(String colourName, int colourValue) {
            mColourName = colourName;
            mColourValue = colourValue;
        }

        @Override
        public int compareTo(Colour colour) {
            return mColourName.compareTo(colour.mColourName);
        }
    }

}
