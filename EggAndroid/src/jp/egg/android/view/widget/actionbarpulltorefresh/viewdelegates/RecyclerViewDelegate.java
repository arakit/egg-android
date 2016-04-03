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

    public static final Class[] SUPPORTED_VIEW_CLASSES = {RecyclerView.class};
    protected static final String TAG = "RecyclerViewDelegate";

    private static AppBarLayout findFirstAppBarLayout(List<View> views) {
        int i = 0;

        for (int z = views.size(); i < z; ++i) {
            View view = views.get(i);
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
            } else if (firstVisiblePosition == RecyclerView.NO_POSITION) {
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
            } else if (firstVisiblePosition == RecyclerView.NO_POSITION) {
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

//                for (int i =0; i<coordinatorLayout.getChildCount(); i++) {
//                    View c = coordinatorLayout.getChildAt(i);
//                    CoordinatorLayout.LayoutParams scrollingParams =
//                            (c.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) ?
//                                    (CoordinatorLayout.LayoutParams) c.getLayoutParams() : null;
//                    Log.d(TAG, "no." +i + " " +c+ " c.getLayoutParams() = " + c.getLayoutParams() + " "
//                            + ", behavior="+(scrollingParams!=null ? scrollingParams.getBehavior() : null)
//                                    + ", offset=" + (scrollingParams!=null && scrollingParams.getBehavior() instanceof AppBarLayout.Behavior ? ((AppBarLayout.Behavior) scrollingParams.getBehavior()).getTopAndBottomOffset() : null)
//                    );
//                }

                final View scrolling = (View) vpPrev;

                CoordinatorLayout.LayoutParams scrollingParams =
                        (scrolling.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) ?
                                (CoordinatorLayout.LayoutParams) scrolling.getLayoutParams() : null;

                if (scrollingParams != null) {
                    final AppBarLayout.ScrollingViewBehavior scrollingViewBehavior =
                            (scrollingParams.getBehavior() instanceof AppBarLayout.ScrollingViewBehavior) ?
                                    (AppBarLayout.ScrollingViewBehavior) scrollingParams.getBehavior() : null;
                    if (scrollingViewBehavior != null) {
                        //int scrollingOffset = scrollingViewBehavior.getTopAndBottomOffset();
                        //Log.d(TAG, "scrollingOffset = " + scrollingOffset + ", scrollingViewBehavior =" + scrollingViewBehavior + ", scrollingParams = " + scrollingParams + ", scrolling = " + scrolling);

                        List<View> children = coordinatorLayout.getDependencies(scrolling);
                        AppBarLayout appBar = findFirstAppBarLayout(children);
                        //Log.d(TAG, "getDependencies  = " + children+ " "+children.size());

                        if (appBar != null) {
                            AppBarLayout.Behavior appBarBehavior =
                                    (appBar.getLayoutParams() instanceof CoordinatorLayout.LayoutParams && ((CoordinatorLayout.LayoutParams) appBar.getLayoutParams()).getBehavior() instanceof AppBarLayout.Behavior)
                                            ? (AppBarLayout.Behavior) ((CoordinatorLayout.LayoutParams) appBar.getLayoutParams()).getBehavior() : null;
                            if (appBarBehavior != null) {
                                int totalScrollRange = appBar.getTotalScrollRange();
                                int appBarHeight = appBar.getHeight();
                                int scrollingOffset = appBarBehavior.getTopAndBottomOffset();
                                //Log.d(TAG, "appBar = "+appBar+ ", " + scrollingOffset + " / " + totalScrollRange + ", h=" +appBarHeight);

                                if (scrollingOffset != 0) {
                                    return false;
                                }
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