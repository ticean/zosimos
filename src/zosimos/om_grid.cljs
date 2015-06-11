(ns ^:figwheel-always zosimos.om-grid)

(defn calc-position
  "Returns calculated pixel position on the page given position and dimensions
  in grid units. Grid units are zero indexed and provided in a map of form:

    :options    Map of container options.
    :x          X coordinate in grid units.
    :y          Y coordinate in grid units.
    :w          Width in grid units.
    :h          Height in grid units.

  Example usage:
  (grid/calc-position {:x 0 :y 0 :w 4 :h 1
                       :options {:container-width 1000
                                 :columns 4
                                 :margin [10 10]
                                 :row-height 150}})"
  [{:keys [item layout-config] :as args}]
  (let [{:keys [container-width columns margin row-height]} layout-config
        {:keys [x y w h]} item
        [x-margin y-margin] margin
        width (- container-width x-margin)]
    {:left   (+ (* width (/ x columns)) x-margin)
     :top    (+ (* row-height y) y-margin)
     :width  (- (* width (/ w columns)) x-margin)
     :height (- (* h row-height) y-margin)}))
