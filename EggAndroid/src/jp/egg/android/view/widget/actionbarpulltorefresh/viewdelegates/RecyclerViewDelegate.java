/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.egg.android.view.widget.actionbarpulltorefresh.viewdelegates;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewParent;

import java.util.List;

import jp.egg.android.util.Log;

/**
 * FIXME
 */
public class RecyclerViewDelegate implements ViewDelegate {

    protected static final String TAG = "RecyclerViewDelegate";

    public static final Class[] SUPPORTED_VIEW_CLASSES = {RecyclerView.class};

    private static AppBarLayout findFirstAppBarLayout(List<View> views) {
        int i = 0;

        for (int z = views.size(); i < z; ++i) {
            View view = (View) views.get(i);
            if (view instanceof AppBarLayout) {
                return (AppBarLayout) view;
            }
        }

        return null;
    }

    @Override
    public boolean isReadyForPull(View view, final float x, final float y) {
        boolean ready = false;

        // First we check whether we're scrolled to the top
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int firstVisiblePosition;
            if (linearLayoutManager.getItemCount() == 0) {
                ready = true;
            } else if ((firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition()) == 0) {
                final View firstVisibleChild = linearLayoutManager.getChildAt(0);
                ready = firstVisibleChild != null && firstVisibleChild.getTop() >= linearLayoutManager.getPaddingTop();
            } else if (firstVisiblePosition==RecyclerView.NO_POSITION) {
                ready = true;
            } else {
                for (int i = firstVisiblePosition - 1; i >= 0; i--) {
                    View child = linearLayoutManager.getChildAt(i);
                    if (child == null || child.getHeight() != 0) {
                        break;
                    }
                    if (i == 0) {
                        ready = true;
                    }
                }
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int firstVisiblePosition;
            if (staggeredGridLayoutManager.getItemCount() == 0) {
                ready = true;
            } else if ((firstVisiblePosition = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0]) == 0) {
                final View firstVisibleChild = staggeredGridLayoutManager.getChildAt(0);
                ready = firstVisibleChild != null && firstVisibleChild.getTop() >= staggeredGridLayoutManager.getPaddingTop();
            } else if (firstVisiblePosition==RecyclerView.NO_POSITION) {
                ready = true;
            } else {
                for (int i = firstVisiblePosition - 1; i >= 0; i--) {
                    View child = staggeredGridLayoutManager.getChildAt(i);
                    if (child == null || child.getHeight() != 0) {
                        break;
                    }
                    if (i == 0) {
                        ready = true;
                    }
                }
            }
        }


        if (ready) {
            ready = checkCoordinatorLayoutTop(recyclerView);
        }

        return ready;
    }

    /**
     * @param recyclerView
     * @return スクロール状態が一番端か
     */
    private boolean checkCoordinatorLayoutTop(RecyclerView recyclerView) {

        ViewParent vp, vpPrev;
        vp = recyclerView.getParent();
        vpPrev = null;
        while (vp != null) {

            if (vp instanceof CoordinatorLayout) {
                CoordinatorLayout coordinatorLayout = (CoordinatorLayout) vp;
                View scrolling = (View) vpPrev;

                CoordinatorLayout.LayoutParams scrollingParams =
                        (scrolling.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) ?
                                (CoordinatorLayout.LayoutParams) scrolling.getLayoutParams() : null;

                if (scrollingParams != null) {
                    AppBarLayout.ScrollingViewBehavior scrollingViewBehavior =
                            (scrollingParams.getBehavior() instanceof AppBarLayout.ScrollingViewBehavior) ?
                                    (AppBarLayout.ScrollingViewBehavior) scrollingParams.getBehavior() : null;
                    if (scrollingViewBehavior != null) {
                        int scrollingOffset = scrollingViewBehavior.getTopAndBottomOffset();

                        List<View> children = coordinatorLayout.getDependencies(scrolling);
                        AppBarLayout appBar = findFirstAppBarLayout(children);

                        if (appBar != null) {
                            int appBarHeight = appBar.getHeight();

                            if (scrollingOffset != appBarHeight) {
                                return false;
                            }
                        }
                    }
                }

            }

            vpPrev = vp;
            vp = vp.getParent();
        }

        return true;
    }

    private void debugViewInfo(String name, View v) {
        int top = v.getTop();
        int scroll = v.getScrollY();
        float translateY = v.getTranslationY();
        Log.d("RecyclerViewDelegate", name + " v = " + v + ", top = " + top + ", translateY = " + translateY + ", scrollY = " + scroll);
    }

}