package com.github.heinrichwizardkreuser.tasktrack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

/**
 * This code was copied from
 * https://www.journaldev.com/23208/android-recyclerview-drag-and-drop
 */
class ItemMoveCallback extends ItemTouchHelper.Callback {


  Context mContext;
  private Paint mClearPaint;
  private ColorDrawable mBackground;
  private int backgroundColor;
  private Drawable deleteDrawable;
  private int intrinsicWidth;
  private int intrinsicHeight;

  private final ItemTouchHelperContract mAdapter;

  public ItemMoveCallback(ItemTouchHelperContract adapter) {
    mContext = MainActivity.appContext;

    mAdapter = adapter;

    mBackground = new ColorDrawable();
    backgroundColor = Color.parseColor("#b80f0a");
    mClearPaint = new Paint();
    mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_baseline_delete_24);
    intrinsicWidth = deleteDrawable.getIntrinsicWidth();
    intrinsicHeight = deleteDrawable.getIntrinsicHeight();
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return true;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

    // Remove swiped item from list and notify the RecyclerView
    int position = viewHolder.getAdapterPosition();

    //switch (swipeDir) {
    //  case ItemTouchHelper.RIGHT: {
    //
    //  }
    //}

    Snackbar.make(viewHolder.itemView,
            "pos = " + position + ", swipeDir=" + swipeDir,
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
  }


  /*not called*/
  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
    int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

    Snackbar.make(recyclerView,
            "getMovementFlags",
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

    return makeMovementFlags(dragFlags, swipeFlags);
  }

  /*not called*/
  @Override
  public boolean onMove(RecyclerView recyclerView,
              RecyclerView.ViewHolder viewHolder,
              RecyclerView.ViewHolder target) {
    mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());


    Snackbar.make(recyclerView,
            "onMove",
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

    //return true;
    return false;
  }

  /*called when item picked up and dropped - note that when dropped, viewholder itemview is null*/
  @Override
  public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (viewHolder instanceof TrackerAdapter.ViewHolder) {
        TrackerAdapter.ViewHolder myViewHolder = (TrackerAdapter.ViewHolder)viewHolder;
        mAdapter.onRowSelected(myViewHolder);
      }
    }

    super.onSelectedChanged(viewHolder, actionState);

    if (viewHolder != null) {
      Snackbar.make(viewHolder.itemView,
              "onSelectedChanged",
              Snackbar.LENGTH_LONG)
              .setAction("Action", null).show();
    }


  }

  /*called when item dropped*/
  @Override
  public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);

    if (viewHolder instanceof TrackerAdapter.ViewHolder) {
      TrackerAdapter.ViewHolder myViewHolder = (TrackerAdapter.ViewHolder)viewHolder;
      mAdapter.onRowClear(myViewHolder);
    }

    Snackbar.make(viewHolder.itemView,
            "clearView",
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

  }

  public interface ItemTouchHelperContract {
    void onRowMoved(int fromPosition, int toPosition);
    void onRowSelected(TrackerAdapter.ViewHolder myViewHolder);
    void onRowClear(TrackerAdapter.ViewHolder myViewHolder);
  }

  /*********************************************************/

  @Override
  public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    View itemView = viewHolder.itemView;
    int itemHeight = itemView.getHeight();

    boolean isCancelled = dX == 0 && !isCurrentlyActive;

    if (isCancelled) {
      clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      return;
    }

    mBackground.setColor(backgroundColor);
    mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
    mBackground.draw(c);

    int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
    int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
    int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
    int deleteIconRight = itemView.getRight() - deleteIconMargin;
    int deleteIconBottom = deleteIconTop + intrinsicHeight;


    deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
    deleteDrawable.draw(c);

    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


  }

  private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
    c.drawRect(left, top, right, bottom, mClearPaint);
  }

  @Override
  public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
    return 0.7f;
  }

  ///*called when item slides across another item*/
  //@Override
  //public void onMoved(@NonNull RecyclerView recyclerView,
  //                    @NonNull RecyclerView.ViewHolder viewHolder,
  //                    int fromPos,
  //                    @NonNull RecyclerView.ViewHolder target,
  //                    int toPos,
  //                    int x,
  //                    int y) {
  //  //super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
  //
  //  final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
  //  if (layoutManager instanceof ItemTouchHelper.ViewDropHandler) {
  //    ((ItemTouchHelper.ViewDropHandler) layoutManager).prepareForDrop(viewHolder.itemView,
  //            target.itemView, x, 0);
  //  }
  //
  //    assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
  //    ensureLayoutState();
  //    resolveShouldLayoutReverse();
  //    final int myPos = getPosition(viewHolder.itemView);
  //    final int targetPos = getPosition(target);
  //    final int dropDirection = myPos < targetPos ? LayoutState.ITEM_DIRECTION_TAIL
  //            : LayoutState.ITEM_DIRECTION_HEAD;
  //    if (mShouldReverseLayout) {
  //      if (dropDirection == LayoutState.ITEM_DIRECTION_TAIL) {
  //        scrollToPositionWithOffset(targetPos,
  //                mOrientationHelper.getEndAfterPadding()
  //                        - (mOrientationHelper.getDecoratedStart(target)
  //                        + mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView)));
  //      } else {
  //        scrollToPositionWithOffset(targetPos,
  //                mOrientationHelper.getEndAfterPadding()
  //                        - mOrientationHelper.getDecoratedEnd(target));
  //      }
  //    } else {
  //      if (dropDirection == LayoutState.ITEM_DIRECTION_HEAD) {
  //        scrollToPositionWithOffset(targetPos, mOrientationHelper.getDecoratedStart(target));
  //      } else {
  //        scrollToPositionWithOffset(targetPos,
  //                mOrientationHelper.getDecoratedEnd(target)
  //                        - mOrientationHelper.getDecoratedMeasurement(viewHolder.itemView));
  //      }
  //    }
  //
  //
  //
  //  Snackbar.make(recyclerView,
  //          "onMoved x=" + x + ", y=" + y,
  //          Snackbar.LENGTH_LONG)
  //          .setAction("Action", null).show();
  //
  //}

}

