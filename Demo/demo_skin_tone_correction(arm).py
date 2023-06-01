import os
import csv

import joblib
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

# Change mean and var with model. e.g. If model is model_skin_tone(L)/(a)/(b)_corr, choosing mean_L/a/b and var_L/a/b. i.e. If model is model_skin_tone(a)_corr, choosing mean_a and var_a.
mean_L = [57.8125, 135.83333333, 128.06249999, 309.6875]
var_L = [22.83289932, 141.0069445, 230.93706602, 10632.71484375]
mean_a = [6.47058824, 132.75490196, 124.47058823, 317.64705882]
var_a = [6.39619377e+00, 2.54964437e+02, 3.65879854e+02, 1.12003460e+04]
mean_b = [7.35185185, 137.00925926, 128.86111109, 316.66666667]
var_b = [7.33916324e+00, 1.43476766e+02, 2.32500772e+02, 1.21388889e+04]
X = pd.read_csv(
    "skin_tone_arm(L).csv")  # corresponding data should be chosen to choosed model. e.g. "skin_tone_arm(L)/(a)/(b).csv" is corresponding with "model_skin_tone(L)/(a)/(b)_corr(arm).h5"
label_final = X[
    "Label (L)"].copy()  # Change parameter with test data, e.g. X["Label (L)/(a)/(b)"] is corresponding with  "skin_tone_arm(L)/(a)/(b).csv"
X = X.drop("Label (L)",
           axis=1)  # Change parameter with test data, e.g. "Label (L)/(a)/(b)" is corresponding with  "skin_tone_arm(L)/(a)/(b).csv"
model = joblib.load(
    "model_skin_tone(L)_corr(arm).h5")  # Change model with test data, e.g. model_skin_tone(L)/(a)/(b)_corr(arm).h5 is corresponding with  "skin_tone_arm(L)/(a)/(b).csv"
X_array = np.array(X)
X_std = (X_array - mean_L) / np.sqrt(var_L)
X_std[:, 0] = X[
    "L"].array  # Change X["L"] with model. e.g. If model is model_skin_tone(L)_corr, choosing X["L"]. If model is model_skin_tone(a)_corr, choosing X["a"].
result = model.predict(X_std)  # Now you can obtain result of correction for skin tone
