(ns ^:figwheel-always zosimos.core
    (:require
      [om.core :as om]
      [om-tools.core :refer-macros [defcomponent]]
      [om-tools.dom :as dom :include-macros true]
      [sablono.core :as html :refer-macros [html]]))

(enable-console-print!)

(defonce app-state
  (atom
    {:meta {}
     :components [{:type :form}
                  {:type :image}
                  {:type :content}
                  {:type :embedly}
                  {:type :iframe}
                  {:type :facebook-like-button}]}))

(defcomponent widget
  "The most basic feature component that can be added to a layout."
  [data owner]
  (render [_]
    (html [:div.widget {:class (name (:type data))}
             (str "Widget " (:type data))])))

(defcomponent app
  "The main application component."
  [data owner]
  (render [_]
    (html
      [:div {:class "content"}
       (om/build-all widget (:components data))])))

(om/root app @app-state
  {:target (. js/document (getElementById "app"))})
