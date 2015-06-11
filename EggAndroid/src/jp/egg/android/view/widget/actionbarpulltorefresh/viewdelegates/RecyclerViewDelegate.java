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

import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewParent;

import java.util.List;

import jp.egg.android.util.Log;

/**
 * FIXME
 */
public class RecyclerViewDelegate implements ViewDelegate {

    public static final Class[] SUPPORTED_VIEW_CLASSES = {RecyclerView.class};

    @Override
    public boolean isReadyForPull(View view, final float x, final float y) {
        boolean ready = false;

        // First we check whether we're scrolled to the top
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        LinearLayoutManager linearLayoutManager = layoutManager instanceof LinearLayoutManager ? (LinearLayoutManager) layoutManager : null;
        if (linearLayoutManager == null) {
            ready = true;
        } else if (linearLayoutManager.getItemCount() == 0) {
            ready = true;
        } else if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
            final View firstVisibleChild = linearLayoutManager.getChildAt(0);
            ready = firstVisibleChild != null && firstVisibleChild.getTop() >= linearLayoutManager.getPaddingTop();
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
                View content = (View) vpPrev;

                List<View> children = coordinatorLayout.getDependencies(content);
                View appBar = children.get(0);
                int appBarHeight = appBar.getHeight();

                if (content.getY() != appBarHeight) {
                    return false;
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
