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
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emmaguy.cleanstatusbar.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

// Adapted from DashClock: https://code.google.com/p/dashclock/source/browse/main/src/main/java/com/google/android/apps/dashclock/configuration/ColorPreference.java
public class ColourPreference extends Preference {
    private static ArrayList<Colour> mColours = new ArrayList<Colour>();

    private int mValue = 0;

    public ColourPreference(Context context) {
        this(context, null);
    }

    public ColourPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.colour_preference_row);
    }

    private String getColoursKey() {
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

        String colours = getSharedPreferences().getString(getColoursKey(), "");

        if (TextUtils.isEmpty(colours)) {
            String[] colourNames = getContext().getResources().getStringArray(R.array.default_colour_choices);
            String[] colourValues = getContext().getResources().getStringArray(R.array.default_colour_choice_values);
            for (int i = 0; i < colourValues.length; i++) {
                mColours.add(new Colour(colourNames[i], Color.parseColor(colourValues[i])));
            }

            if(mColours.size() > 0) {
                Colour defaultValue = mColours.get(0);
                mValue = defaultValue.mColourValue;
                persistInt(mValue);
            }

            getSharedPreferences().edit().putString(getColoursKey(), new Gson().toJson(mColours)).apply();
        } else {
            mColours = new Gson().fromJson(colours, new TypeToken<ArrayList<Colour>>() {}.getType());
        }

        setColourValue((ImageView) view.findViewById(R.id.colour_view), mValue);
        ((TextView) view.findViewById(R.id.colour_name)).setText(getTitle());
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedInt(0) : (Integer) defaultValue);
    }

    public void setValue(int value) {
        if (callChangeListener(value)) {
            mValue = value;
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

        private String mNewColourName;

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
                    Colour item = (Colour) mAdapter.getItem(position);
                    mPreference.setValue(item.mColourValue);
                    dismiss();
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

        private void showAddColourDialog() {
            final EditText input = new EditText(getActivity());
            input.setHint(R.string.hint_theme_blue);

            new AlertDialog.Builder(getActivity())
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.add_new_colour, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            showEnterColourValueDialog();

                            mNewColourName = input.getText().toString();
                        }
                    })
                    .setTitle(R.string.add_new_colour)
                    .setMessage(R.string.enter_the_name_of_your_colour)
                    .setView(input)
                    .create()
                    .show();
        }

        private void showEnterColourValueDialog() {
            final EditText input = new EditText(getActivity());
            input.setHint(R.string.hint_colour_no_hash_char);

            new AlertDialog.Builder(getActivity())
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.add_new_colour, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String colourValue = input.getText().toString();

                            try {
                                String colourString = colourValue;
                                if (!colourValue.startsWith("#")) {
                                    colourString = "#" + colourValue;
                                }

                                int colour = Color.parseColor(colourString);

                                mColours.add(new Colour(mNewColourName, colour));
                                mPreference.getSharedPreferences().edit().putString(mPreference.getColoursKey(), new Gson().toJson(mColours)).apply();

                                hideKeyboard(getActivity(), input);
                            } catch (IllegalArgumentException e) {
                                Toast.makeText(getActivity(), R.string.invalid_hex_colour, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setTitle(R.string.add_new_colour)
                    .setMessage(R.string.enter_the_hex_value_of_your_colour)
                    .setView(input)
                    .create()
                    .show();
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
        public Object getItem(int position) {
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

            Colour colour = mColours.get(position);
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

    private static void setColourValue(ImageView imageView, int colourResId) {
        Drawable currentDrawable = imageView.getDrawable();
        GradientDrawable colorChoiceDrawable;
        if (currentDrawable != null && currentDrawable instanceof GradientDrawable) {
            // reuse drawable
            colorChoiceDrawable = (GradientDrawable) currentDrawable;
        } else {
            colorChoiceDrawable = new GradientDrawable();
            colorChoiceDrawable.setShape(GradientDrawable.OVAL);
        }

        colorChoiceDrawable.setColor(colourResId);
        imageView.setImageDrawable(colorChoiceDrawable);
    }

    static class Colour {
        int mColourValue;
        String mColourName;

        public Colour(String colourName, int colourValue) {
            mColourName = colourName;
            mColourValue = colourValue;
        }
    }
}
