package com.pnuema.simplebible.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.pnuema.simplebible.R;
import com.pnuema.simplebible.data.IChapter;
import com.pnuema.simplebible.data.IChapterProvider;
import com.pnuema.simplebible.retrievers.BaseRetriever;
import com.pnuema.simplebible.retrievers.DBTRetriever;
import com.pnuema.simplebible.statics.CurrentSelected;
import com.pnuema.simplebible.ui.dialogs.BCVSelectionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A fragment representing a list of chapter numbers to pick from.
 * <p/>
 */
public class ChapterSelectionFragment extends Fragment implements Observer {
    private BCVSelectionListener mListener;
    private final List<IChapter> mChapters = new ArrayList<>();
    private BaseRetriever mRetriever = new DBTRetriever(); //TODO have this select which retriever based on version
    private GridView mGridView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChapterSelectionFragment() {
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && CurrentSelected.getBook() != null) {
            mRetriever.getChapters(getContext(), CurrentSelected.getBook().getId());
        }
    }

    @SuppressWarnings("unused")
    public static ChapterSelectionFragment newInstance(BCVSelectionListener listener) {
        return new ChapterSelectionFragment().setListener(listener);
    }

    private ChapterSelectionFragment setListener(BCVSelectionListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGridView = (GridView) inflater.inflate(R.layout.fragment_number_list, container, false);
        return mGridView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRetriever.addObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRetriever.deleteObservers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void update(Observable observable, Object o) {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        mChapters.clear();
        if (o instanceof IChapterProvider) {
            //noinspection unchecked
            mChapters.addAll(((IChapterProvider)o).getChapters());
        }

        List<Integer> mList = new ArrayList<>();
        for (int i = 1; i <= mChapters.size(); i++) {
            mList.add(i);
        }

        mGridView.setAdapter(new ArrayAdapter<>(activity, R.layout.item_number, mList));
        mGridView.setOnItemClickListener((adapterView, view, i, l) -> mListener.onChapterSelected(mChapters.get(i)));
    }
}
