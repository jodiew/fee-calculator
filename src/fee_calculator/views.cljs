(ns fee-calculator.views
  (:require
   [reagent.core :refer [as-element with-let]]
   [re-frame.core :refer [subscribe dispatch]]

   [reagent-mui.styles :refer [theme-provider create-theme]]
   [reagent-mui.material.css-baseline :refer [css-baseline]]
   [reagent-mui.material.container :refer [container]]
   [reagent-mui.material.paper :refer [paper]]
   [reagent-mui.material.typography :refer [typography]]
   [reagent-mui.material.stepper :refer [stepper]]
   [reagent-mui.material.step :refer [step]]
   [reagent-mui.material.step-label :refer [step-label]]
   [reagent-mui.material.button :refer [button]]
   [reagent-mui.material.box :refer [box]]

   [fee-calculator.subs :as subs]
   [fee-calculator.events :as events]
   [fee-calculator.routes :as routes]
   [fee-calculator.styles :as styles]
   [fee-calculator.components.select-product-step :refer [select-product-step]]
   [fee-calculator.components.enter-amount-step :refer [enter-amount-step]]
   [fee-calculator.components.create-portfolio-step :refer [create-portfolio-step]]
   [fee-calculator.components.view-fees-step :refer [view-fees-step]]))

(defn home-panel []
  (let [active-step @(subscribe [::subs/active-step])]
    [theme-provider (create-theme {})
     [css-baseline]
     [container {:component "main"
                 :max-width "md" 
                 :sx {:my 4}}
      [paper {:variant "outlined"
              :sx {:my 6
                   :p 3}}
       [typography {:variant "h4"
                    :align "center"}
        "Fee Calculator"]
       [stepper {:active-step active-step
                 :sx {:pt 3
                      :pb 5}}
        [step
         [step-label
          {:optional (as-element [typography
                                  {:variant "caption"}
                                  @(subscribe [::subs/product-formatted])])}
          "Select solution"]]
        [step
         [step-label
          {:optional (as-element [typography
                                  {:variant "caption"}
                                  @(subscribe [::subs/investment-amount-formatted])])}
          "Enter amount"]]
        [step
         [step-label
          "Create portfolio"]]
        [step
         [step-label
          "View fees"]]]
       [:<>
        (case active-step
          0 [select-product-step]
          1 [enter-amount-step]
          2 [create-portfolio-step]
          3 [view-fees-step])
        [box {:sx {:display "flex"
                   :justify-content "space-between"}}
         (when @(subscribe [::subs/show-back?])
           [button {:sx {:mt 3}
                    :on-click #(dispatch [::events/handle-back])}
            "Back"])
         (if @(subscribe [::subs/show-print?])
           [button {:sx {:mt 3}
                    :variant "contained"
                    :on-click #(dispatch [::events/navigate :print])}
            "Print"]
           [button {:sx {:mt 3}
                    :on-click #(dispatch [::events/handle-next])}
            "Next"])]]]]]))

(defmethod routes/panels :home-panel []
  [home-panel])

(defn print-panel []
  (with-let [handler #(dispatch [::events/navigate :home])
             _ (.addEventListener js/window "afterprint" handler)
             _ (js/setTimeout #(.print js/window) 1)]
    [:div {:class (styles/print-panel)}
     [view-fees-step]]
    (finally
      (.removeEventListener js/window "afterprint" handler))))

(defmethod routes/panels :print-panel []
  [print-panel])

(defn main-panel []
  (let [active-panel (subscribe [::subs/active-panel])]
    (routes/panels @active-panel)))