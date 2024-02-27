import {
    ADD_CATEGORY_FILTER,
    ADD_DIVISION_FILTER,
    ADD_PRICE_FILTER,
    ADD_SORT_FILTER,
    ADD_SUBCATEGORY_FILTER,
    CLEAR_ALL_FILTER,
    DELETE_INDIVIDUAL_PRODUCT
} from "@/Actions/constants";

export const addCategory = (category) => {
    return {
        type: ADD_CATEGORY_FILTER,
        payload: category
    }
}
export const addSubCategory = (subcategory) => {
    return {
        type: ADD_SUBCATEGORY_FILTER,
        payload: subcategory
    }
}
export const addDivision = (division) => {
    return {
        type: ADD_DIVISION_FILTER,
        payload: division
    }
}
export const addPrice = (price) => {
    return {
        type: ADD_PRICE_FILTER,
        payload: price
    }
}
export const addSort = (sort) => {
    return {
        type: ADD_SORT_FILTER,
        payload: sort
    }
}
export const clearAll = () => {
    return {
        type: CLEAR_ALL_FILTER,
    }
}
export const deleteIndividual = (data) => {
    return {
        type: DELETE_INDIVIDUAL_PRODUCT,
        payload: data
    }
}