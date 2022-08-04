(ns fee-calculator.components.create-portfolio-step
  (:require-macros [reagent-mui.util :refer [react-component]])
  (:require [re-frame.core :refer [subscribe dispatch]]

            [reagent-mui.material.dialog-title :refer [dialog-title]]
            [reagent-mui.material.dialog-content :refer [dialog-content]]
            [reagent-mui.material.dialog-actions :refer [dialog-actions]]
            [reagent-mui.material.grid :refer [grid]]
            [reagent-mui.material.text-field :refer [text-field]]
            [reagent-mui.material.autocomplete :refer [autocomplete]]
            [reagent-mui.material.list :refer [list]]
            [reagent-mui.material.list-item :refer [list-item]]
            [reagent-mui.material.list-item-icon :refer [list-item-icon]]
            [reagent-mui.material.list-item-text :refer [list-item-text]]
            [reagent-mui.material.list-item-button :refer [list-item-button]]
            [reagent-mui.material.icon-button :refer [icon-button]]
            [reagent-mui.material.button :refer [button]]
            [reagent-mui.material.tooltip :refer [tooltip]]
            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.collapse :refer [collapse]]
            [reagent-mui.material.divider :refer [divider]]
            [reagent-mui.material.dialog :refer [dialog]]
            [reagent-mui.material.box :refer [box]]
            [reagent-mui.material.paper :refer [paper]]
            [reagent-mui.icons.add-circle :refer [add-circle]]
            [reagent-mui.icons.add-circle-outline :refer [add-circle-outline]]
            [reagent-mui.icons.remove-circle-outline :refer [remove-circle-outline]]
            [reagent-mui.icons.paid :refer [paid]]
            [reagent-mui.icons.expand-less :refer [expand-less]]
            [reagent-mui.icons.expand-more :refer [expand-more]]

            [fee-calculator.subs :as subs]
            [fee-calculator.events :as events]
            [fee-calculator.components.custom-text-fields :refer [currency-text-field percent-text-field]]))


(defn securities-panel []
  [dialog {:open @(subscribe [::subs/open-securitites])
           :on-close #(dispatch [::events/open-securitites false])
           :full-width true
           :max-width "lg"}
   [dialog-title "Search and select funds"]
   [dialog-content
    [grid {:container true
           :align-items "center"
           :spacing 2
           :sx {:pt 1}}
     [grid {:item true
            :xs 12
            :sm 4}
      [text-field {:label "Search by fund name"
                   :full-width true
                   :value @(subscribe [::subs/search-string])
                   :on-change #(dispatch [::events/search-string (.. % -target -value)])}]]
     [grid {:item true
            :xs 12
            :sm 3}
      [autocomplete {:multiple true
                     :options (clj->js [{:label "Company Funds"
                                         :category "COMPANY-FUND"}
                                        {:label "Other investment options"
                                         :category "MANAGED-FUND"}])
                     :value @(subscribe [::subs/admin-fee-option])
                     :is-option-equal-to-value (fn [option value]
                                                 (= (js->clj value) (js->clj option)))
                     :on-change (fn [_ v _] (dispatch [::events/admin-fee-option v]))
                     :render-input (react-component [props]
                                                    [text-field (merge props
                                                                       {:label "and/or admin fees"})])
                     :full-width true}]]
     [grid {:item true
            :xs 12
            :sm 4}
      [autocomplete {:multiple true
                     :options (clj->js @(subscribe [::subs/asset-classes]))
                     :value @(subscribe [::subs/asset-class-option])
                     :on-change (fn [_ v _] (dispatch [::events/asset-class-option v]))
                     :render-input (react-component [props]
                                                    [text-field (merge props
                                                                       {:label "and/or asset class"})])
                     :full-width true}]]
     [grid {:item true
            :xs 12
            :sm 1}
      [button
       {:variant "outlined"
        :on-click #(dispatch [::events/clear-search])}
       "Clear"]]]
    [list
     (for [fund @(subscribe [::subs/filtered-securities])]
       [list-item {:key (:issuer-code fund)}
        [list-item-icon [icon-button
                         {:disabled (:in-use? fund)
                          :on-click #(dispatch [::events/add-allocation fund])}
                         [add-circle-outline]]]
        [list-item-text {:primary (:issuer fund)
                         :secondary (:issuer-code fund)}]
        [paid {:html-color (if (= (:category fund) "COMPANY-FUND")
                             "red"
                             "black")}]])]]
   [dialog-actions
    [button {:on-click #(dispatch [::events/open-securitites false])}
     "Close"]]])

(defn allocation-item
  ([id] [allocation-item {:variant "standard"} id])
  ([{:keys [variant]} id]
   (let [{:keys [name tooltip-text]} @(subscribe [::subs/allocation id])]
     [list-item
      (if (= variant "standard")
        [list-item-icon [icon-button
                         {:on-click #(dispatch [::events/remove-allocation id])}
                         [remove-circle-outline]]]
        [list-item-icon])
      [grid {:container true
             :align-items "center"
             :spacing 3}
       [grid {:item true
              :xs 12
              :sm 4}
        (if (= variant "tooltip")
          [tooltip {:title tooltip-text}
           [list-item-text {:primary name}]]
          [list-item-text {:primary name}])]
       [grid {:item true
              :xs 12
              :sm 4}
        [currency-text-field
         {:sub-path [::subs/allocation-amount id]
          :disp [::events/allocation-amount id]}]]
       [grid {:item true
              :xs 12
              :sm 4}
        [percent-text-field
         {:sub-path [::subs/allocation-percent id]
          :disp [::events/allocation-percent id]}]]]])))

(defn create-portfolio-step []
  (let [open-company-funds @(subscribe [::subs/open-company-funds])
        open-managed-funds @(subscribe [::subs/open-managed-funds])
        company-ids @(subscribe [::subs/company-ids])
        mf-ids @(subscribe [::subs/managed-funds-ids])]
    [:<>
     [typography {:variant "h6"
                  :gutter-bottom true}
      "Create your portfolio"]
     [list
      [list-item-button {:on-click #(dispatch [::events/toggle-company-funds])}
       [list-item-icon {:sx {:p 1}}
        (if open-company-funds [expand-less] [expand-more])]
       [grid {:container true
              :align-items "center"
              :spacing 3}
        [grid {:item true
               :xs 12
               :sm 4}
         [tooltip
          {:title "We have kept costs low by minimising additional fees on Company Funds. By choosing the Company Funds, you will pay the lowest administration fees of all the investment options available in our Solutions."}
          [list-item-text {:primary "Company Funds"}]]]
        [grid {:item true
               :xs 12
               :sm 4}
         [currency-text-field
          {:sub-path [::subs/company-total-amount]
           :disabled true}]]
        [grid {:item true
               :xs 12
               :sm 4}
         [percent-text-field
          {:sub-path [::subs/company-total-percent]
           :disabled true}]]]]
      [collapse {:in open-company-funds
                 :timeout "auto"
                 :unmount-on-exit true}
       [list {:component "div"
              :disable-padding true}
        [divider]
        (for [id company-ids]
          ^{:key id}
          [allocation-item id])
        (when @(subscribe [::subs/show-add-company-funds?])
          [list-item-button {:on-click #(dispatch [::events/open-securitites true])}
           [list-item-icon {:sx {:p 1}} [add-circle]]
           [list-item-text {:primary "Add more funds"}]])
        [divider]]]
      [allocation-item {:variant "tooltip"} :cash-account]
      [allocation-item {:variant "tooltip"} :term-deposits]
      [allocation-item {:variant "tooltip"} :csx-security]
      [allocation-item {:variant "tooltip"} :managed-account]
      [list-item-button {:on-click #(dispatch [::events/toggle-managed-funds])}
       [list-item-icon {:sx {:p 1}}
        (if open-managed-funds [expand-less] [expand-more])]
       [grid {:container true
              :align-items "center"
              :spacing 3}
        [grid {:item true
               :xs 12
               :sm 4}
         [tooltip
          {:title "You can choose from a large selection of managed funds. The administration fee varies dependent on the choices you make."}
          [list-item-text {:primary "Managed Funds"}]]]
        [grid {:item true
               :xs 12
               :sm 4}
         [currency-text-field
          {:sub-path [::subs/mf-total-amount]
           :disabled true}]]
        [grid {:item true
               :xs 12
               :sm 4}
         [percent-text-field
          {:sub-path [::subs/mf-total-percent]
           :disabled true}]]]]
      [collapse {:in open-managed-funds
                 :timeout "auto"
                 :unmount-on-exit true}
       [list {:component "div"
              :disable-padding true}
        [divider]
        (for [id mf-ids]
          ^{:key id}
          [allocation-item id])
        [list-item-button {:on-click #(dispatch [::events/open-securitites true])}
         [list-item-icon {:sx {:p 1}} [add-circle]]
         [list-item-text {:primary "Add more funds"}]] 
        [divider]]]]
     [box {:sx {:mt 1}}
      [paper {:sx {:p 3}}
       [grid {:container true
              :align-items "center"
              :spacing 3}
        [grid {:item true
               :xs 12
               :sm 4}
         [currency-text-field
          {:label "Investment amount"
           :sub-path [::subs/investment-amount]
           :disp [::events/investment-amount]}]]
        [grid {:item true
               :xs 12
               :sm 3}
         [currency-text-field
          {:label "Unallocated amount"
           :disabled true
           :sub-path [::subs/unallocated-amount]}]]
        [grid {:item true
               :xs 12
               :sm 3}
         [percent-text-field
          {:label "Unallocated percent"
           :disabled true
           :sub-path [::subs/unallocated-percent]}]]
        [grid {:item true
               :xs 12
               :sm 2}
         [button {:variant "outlined"
                  :full-width true
                  :on-click #(dispatch [::events/reset-allocations])}
          "Clear All"]]]]]
     [securities-panel]]))
