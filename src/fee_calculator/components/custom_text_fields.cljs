(ns fee-calculator.components.custom-text-fields
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.input-adornment :refer [input-adornment]]
            
            [fee-calculator.helpers :refer [number->decimal
                                            decimal->number
                                            number->percent
                                            percent->number]]))

;; -- Currency Text Field ------------------------------------------

(defn currency-text-field [{:keys [sub-path disp disabled label]}]
  (r/with-let [value    (r/atom nil)
               focused? (r/atom false)]
     [text-field
      {:type      "text"
       :full-width true
       :disabled disabled
       :label label
       :on-focus  #(do (reset! value @(subscribe sub-path))
                       (reset! focused? true))
       :on-blur   #(do (dispatch (conj disp @value))
                       (reset! focused? false))
       :on-key-down #(case (.-which %)
                       13 (dispatch (conj disp @value))
                       nil)
       :value     (number->decimal (if @focused? @value @(subscribe sub-path)))
       :on-change (fn [e]
                    (let [val (-> e .-target .-value)
                          formatted (decimal->number val)]
                      (cond
                        (empty? val) (reset! value 0)
                        (number? formatted) (reset! value formatted)
                        :else nil)))
       :InputProps {:start-adornment
                    (r/as-element [input-adornment
                                   {:position "start"}
                                   "$"])}}]))


;; -- Percent Text Field -------------------------------------------

(defn percent-text-field [{:keys [sub-path disp disabled label]}]
  (r/with-let [value    (r/atom nil)
               focused? (r/atom false)]
    [text-field
     {:type      "text"
      :full-width true
      :disabled disabled
      :label label
      :on-focus  #(do (reset! value (number->percent @(subscribe sub-path)))
                      (reset! focused? true))
      :on-blur   #(do (dispatch (conj disp (percent->number @value)))
                      (reset! focused? false))
      :on-key-down #(case (.-which %)
                      13 (dispatch (conj disp (percent->number @value)))
                      nil)
      :value     (if @focused? @value (number->percent @(subscribe sub-path)))
      :on-change #(reset! value (-> % .-target .-value))
      :InputProps {:end-adornment
                   (r/as-element [input-adornment
                                  {:position "end"}
                                  "%"])}}]))
