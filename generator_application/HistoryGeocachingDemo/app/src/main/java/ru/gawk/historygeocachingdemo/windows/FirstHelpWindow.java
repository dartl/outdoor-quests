package ru.gawk.historygeocachingdemo.windows;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ru.gawk.historygeocachingdemo.R;
import ru.gawk.historygeocachingdemo.adapters.HelpsAdapter;

/**
 * Created by GAWK on 10.12.2017.
 */

public class FirstHelpWindow extends DialogFragment {
    private Dialog mDlg;
    private String mMessage, mTitle, mUrl;

    private TextView mTextView;
    private ImageView mImageView;
    private RelativeLayout mRelativeLayout;

    public FirstHelpWindow(String mMessage, String mTitle, String url) {
        this.mMessage = mMessage;
        this.mTitle = mTitle;
        this.mUrl = url;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.window_first_help, null);

        builder.setView(view);

        builder.setTitle(mTitle).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mTextView = (TextView) view.findViewById(R.id.textViewHelp);
        mImageView = (ImageView) view.findViewById(R.id.imageViewHelp);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.loadingPanel);

        mTextView.setText(mMessage);

        if (!mUrl.equals("")) new AsyncImgDownload().execute(mUrl);
        else mRelativeLayout.setVisibility(View.GONE);

        mDlg = builder.create();
        return mDlg;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    class AsyncImgDownload extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... arg) {
            URL url = null;
            try {
                url = new URL(arg[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                assert url != null;
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            mImageView.setImageBitmap(s);
            mImageView.setVisibility(View.VISIBLE);
            mRelativeLayout.setVisibility(View.GONE);
        }
    }
}
