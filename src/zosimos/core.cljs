(ns ^:figwheel-always zosimos.core
    (:require
      [om.core :as om]
      [om-tools.core :refer-macros [defcomponent defcomponentmethod]]
      [om-tools.dom :as dom :include-macros true]
      [sablono.core :as html :refer-macros [html]]
      [zosimos.om-grid :as grid]))

(enable-console-print!)

(defonce app-state
  (atom
    {:dev {:render-mode "grid"}
     :meta {}
     :layout {:autosize true
              :columns 12
              :container-width 1080
              :margin [10 10]
              :row-height 150
              :draggable? true
              :resizable? true
              :use-css-transforms? true
              :draggable-cancel "no-draggable"
              :draggable-handle "draggable"
              :children [
                ; Row 0
                {:x 0 :y 0 :w 12 :h 1
                 :child-data {:id 1
                              :type :title
                              :user-data {:title "Page Title"}}}
                ; Row 1-2
                {:x 0 :y 1 :w 6 :h 2
                 :child-data {:id 2
                              :type :content
                              :user-data {:content "Item 1"}}}
                {:x 6 :y 1 :w 6 :h 2
                 :child-data {:id 3
                              :type :image
                              :user-data {:src "https://i.imgur.com/pA1tRuV.gif"
                                          :alt "Sniff sniff"}}}
                ; Row 3
                {:x 0 :y 3 :w 6 :h 1
                 :child-data {:id 4
                              :type :embedly
                              :user-data nil}}
                {:x 6 :y 3 :w 6 :h 1
                 :child-data {:id 5
                              :type :facebook-like-button
                              :user-data nil}}
                ; Row 4-6
                {:x 0 :y 4 :w 12 :h 3
                 :child-data {:id 6
                              :type :iframe
                              :user-data {:src "http://imgur.com/"
                                          :width 1060
                                          :height 440}}}
                ; Row 7
                {:x 0 :y 7 :w 12 :h 1
                 :child-data {:id 7
                              :type :form
                              :user-data nil}}]}}))


;; ============================
;; Widget Components
;; ============================

(defmulti widget
  "The most basic feature component that can be added to a layout."
  (fn [data owner] (:type data)))

(defcomponentmethod widget :title
  [data owner]
  (render [_]
    (html [:h1.widget.title-widget
           (-> data :user-data :title)])))

(defcomponentmethod widget :content
  [data owner]
  (render [_]
    (html [:div.widget.content-widget
           (-> data :user-data :content)])))

(defcomponentmethod widget :image
  [data owner]
  (render [_]
    (html [:div.widget.image-widget
           (html/image (-> data :user-data :src)
                       (-> data :user-data :alt))])))

(defcomponentmethod widget :iframe
  [data owner]
  (render [_]
    (let [{:keys [src width height]} (:user-data data)]
    (html [:div.widget.image-widget
           [:iframe {:src src :width width :height height}]]))))

(defcomponentmethod widget :default
  [data owner]
  (render [_]
    (html [:div.widget.default-widget {:class (name (:type data))}
           [:div.title (str "Widget " (:type data))]])))



;; ============================
;; Om Grid Stuff
;; ============================

(def om-grid-responsive-layout-default-props
  {:breakpoints {:lg 1200 :md 996 :sm 768 :xs 480 :xxs 0}
   :cols {:lg 12 :md 10 :sm 6 :xs 4 :xxs 2}
   :layouts {}})

(def om-grid-layout-default-props
  {:autosize true
   :children []
   :columns 12
   :container-width 1080
   :draggable-cancel "no-draggable"
   :draggable-handle "draggable"
   :margin [10 10]
   :row-height 150
   :draggable? true
   :resizable? true
   :use-css-transforms? true})

(def om-grid-item-default-props
  {:child-data nil
   :x 1
   :y 1
   :w 1
   :h 1})

(defcomponent om-grid-item
  "TODO: Inject grid item component dependency!"
  [{:keys [layout-data item] :as data} owner]
  (render [_]
    (let [style (into (grid/calc-position data) {:position "absolute"})]
      (html [:div.om-grid-item {:style style}
             (om/build widget (:child-data item))]))))

(defcomponent om-grid-layout
  "Super magic funtime grid."
  [{:keys [children] :as data} owner]
  (render [_]
    (let [layout-config {:layout-config (dissoc data :children)}
          items (map #(into layout-config {:item %}) children)]
    (html [:div.om-grid-layout
      (om/build-all om-grid-item items)]))))



;; ============================
;; App
;; ============================

(defcomponent app
  "The main application component."
  [data owner]
  (render [_]
    (html
      [:div {:class "app"}
       (if (= "grid" (-> @data :dev :render-mode))
         (om/build om-grid-layout (:layout data))
         (om/build-all widget (:components data)))])))

(om/root app @app-state
  {:target (. js/document (getElementById "app"))})
