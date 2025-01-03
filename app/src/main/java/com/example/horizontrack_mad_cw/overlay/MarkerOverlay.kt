//package com.example.horizontrack_mad_cw.overlay
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.graphics.Point
//import android.graphics.drawable.BitmapDrawable
//import org.osmdroid.api.IMapView
//import org.osmdroid.views.overlay.ItemizedOverlay
//import org.osmdroid.views.overlay.OverlayItem
//import org.osmdroid.util.GeoPoint
//
//class MarkerOverlay(context: Context, val icon: Bitmap,val loc: GeoPoint) : ItemizedOverlay<OverlayItem>(null) {
//    private var items = mutableListOf<OverlayItem>()
//
//    init {
//        var overlayItem = OverlayItem("Custom Icon", "This is a custom icon", loc)
//        overlayItem.setMarker()
//        items.add(overlayItem)
//        populate()
//    }
//
//    override fun createItem(index: Int): OverlayItem {
//        return items[index]
//    }
//
//    override fun size(): Int {
//        return items.size
//    }
//
//    override fun onSnapToItem(
//        x: Int,
//        y: Int,
//        snapPoint: Point?,
//        mapView: IMapView?
//    ): Boolean {
//        TODO("Not yet implemented")
//    }
//
//}