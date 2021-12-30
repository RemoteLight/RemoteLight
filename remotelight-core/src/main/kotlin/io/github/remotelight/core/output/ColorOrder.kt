package io.github.remotelight.core.output

import io.github.remotelight.core.color.Color

enum class ColorOrder(val orderFunction: OrderFunction, val isWhiteSupported: Boolean = false) {

    // RGB
    RGB({ r, g, b, w -> Color(r, g, b, w) }),
    RBG({ r, g, b, w -> Color(r, b, g, w) }),
    GRB({ r, g, b, w -> Color(g, r, b, w) }),
    GBR({ r, g, b, w -> Color(g, b, r, w) }),
    BRG({ r, g, b, w -> Color(b, r, g, w) }),
    BGR({ r, g, b, w -> Color(b, g, r, w) }),

    // RGBW
    WRGB({ r, g, b, w -> Color(w, r, g, b) }, true),
    WRBG({ r, g, b, w -> Color(w, r, b, g) }, true),
    WGRB({ r, g, b, w -> Color(w, g, r, b) }, true),
    WGBR({ r, g, b, w -> Color(w, g, b, r) }, true),
    WBRG({ r, g, b, w -> Color(w, b, r, g) }, true),
    WBGR({ r, g, b, w -> Color(w, b, g, r) }, true),

    RWGB({ r, g, b, w -> Color(r, w, g, b) }, true),
    RWBG({ r, g, b, w -> Color(r, w, b, g) }, true),
    RGWB({ r, g, b, w -> Color(r, g, w, b) }, true),
    RGBW({ r, g, b, w -> Color(r, g, b, w) }, true),
    RBWG({ r, g, b, w -> Color(r, b, w, g) }, true),
    RBGW({ r, g, b, w -> Color(r, b, g, w) }, true),

    GWRB({ r, g, b, w -> Color(g, w, r, b) }, true),
    GWBR({ r, g, b, w -> Color(g, w, b, r) }, true),
    GRWB({ r, g, b, w -> Color(g, r, w, b) }, true),
    GRBW({ r, g, b, w -> Color(g, r, b, w) }, true),
    GBWR({ r, g, b, w -> Color(g, b, w, r) }, true),
    GBRW({ r, g, b, w -> Color(g, b, r, w) }, true),

    BWRG({ r, g, b, w -> Color(b, w, r, g) }, true),
    BWGR({ r, g, b, w -> Color(b, w, g, r) }, true),
    BRWG({ r, g, b, w -> Color(b, r, w, g) }, true),
    BRGW({ r, g, b, w -> Color(b, r, g, w) }, true),
    BGWR({ r, g, b, w -> Color(b, g, w, r) }, true),
    BGRW({ r, g, b, w -> Color(b, g, r, w) }, true)

}

typealias OrderFunction = (r: Int, g: Int, b: Int, w: Int) -> Color

fun ColorOrder.reorderColor(color: Color) = orderFunction(color.red, color.green, color.blue, color.white)

fun Color.applyColorOrder(colorOrder: ColorOrder) = colorOrder.reorderColor(this)
