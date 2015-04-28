(ns ^:figwheel-always zosimos.core
    (:require
      [om.core :as om]
      [om-tools.core :refer-macros [defcomponent defcomponentmethod]]
      [om-tools.dom :as dom :include-macros true]
      [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state
  (atom
    {:dev {:render-mode "grid"}
     :meta {}
     :layout {:autosize true
              :cols 12
              :margin [10 10]
              :row-height 150
              :draggable? true
              :resizable? true
              :use-css-transforms? true
              :draggable-cancel "no-draggable"
              :draggable-handle "draggable"
              :children [
                {:child-data {:type :title
                              :id 1
                              :user-data
                              {:title "Page Title"}}
                 :x 1
                 :y 1
                 :w 1
                 :h 1}
               {:child-data {:type :content
                             :id 2
                             :user-data
                             {:content "This is user content."}}
                 :x 1
                 :y 1
                 :w 1
                 :h 1}]}

     ; Non-grid data model.
     :components [{:type :title
                   :id 1
                   :user-data
                   {:title "Page Title"}}
                  {:type :content
                   :id 2
                   :user-data
                   {:content "This is user content."}}
                  {:type :image
                   :id 3
                   :user-data
                   {:src "https://www.dropbox.com/s/0q37qjigyxtaofh/Screenshot%202015-04-24%2000.12.54.png?dl=1"
                    :alt "Monkey business"}}
                  {:type :embedly
                   :id 4
                   :user-data nil}
                  {:type :iframe
                   :id 5
                   :user-data
                   {:src "http://www.promojam.com"
                    :width "600px"
                    :height "200px"}}
                  {:type :facebook-like-button
                   :id 7
                   :user-data nil}
                  {:type :form
                   :id 8
                   :user-data nil}]}))


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
    (html [:div.widget.image-widget
           [:iframe {:src (-> data :user-data :src)}]])))

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
   :cols 12
   :draggable-cancel "no-draggable"
   :draggable-handle "draggable"
   :margin [10 10]
   :row-height 150
   :draggable? true
   :resizable? true
   :use-css-transforms? true})

(def om-grid-item-default-props
  {:child nil
   :columns: 1
   :container-width 400
   :row-height 150
   :margin [10 10]
   :x 1
   :y 1
   :w 1
   :h 1
   :cancel ""
   :draggable? true
   :resizable? true
   :use-css-transforms? true})

(defcomponent om-grid-item
  "TODO: Inject grid item component dependency!"
  [{:keys [child-data x y w h] :as data} owner]
  (render [_]
    (html [:div.om-grid-item (om/build widget child-data)])))

(defcomponent om-grid-layout
  "Super magic funtime grid."
  [{:keys [children] :as data} owner]
  (render [_]
    (html [:div.om-grid-layout
           (om/build-all om-grid-item children)])))



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
