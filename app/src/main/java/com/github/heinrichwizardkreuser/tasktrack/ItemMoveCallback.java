package com.github.heinrichwizardkreuser.tasktrack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

/**
 * This code was copied from
 * https://www.journaldev.com/23208/android-recyclerview-drag-and-drop
 */
class ItemMoveCallback extends ItemTouchHelper.Callback {

  private final ItemTouchHelperContract mAdapter;

  public ItemMoveCallback(ItemTouchHelperContract adapter) {
    mAdapter = adapter;
  }

  @Override
  public boolean isLongPressDragEnabled() {
    return true;
  }

  @Override
  public boolean isItemViewSwipeEnabled() {
    return false;
  }

  @Override
  public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {

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

  /*not called*/
  @Override
  public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
    int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;


    Snackbar.make(recyclerView,
            "getMovementFlags",
            Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();

    return makeMovementFlags(dragFlags, 0);
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

    return true;
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

}

