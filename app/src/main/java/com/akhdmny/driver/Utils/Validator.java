package com.akhdmny.driver.Utils;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Validator {
    public enum Rules {
        MIN, MAX, CONFIRMED, EMAIL, NUMERIC, REQUIRED
    }

    private Context _context;
    private boolean _showToast;
    private Rules[] _rules;
    private boolean _isValid;
    private List<String> _messages;

    public Validator(Context context) {
        _context = context;
        _showToast = true;
        _isValid = true;
        _messages = new ArrayList<>();
    }

    public Validator(Context context, boolean showToast) {
        _context = context;
        _showToast = showToast;
        _isValid = true;
        _messages = new ArrayList<>();
    }

    public Validator setRules(Rules... rules) {
        _rules = rules;

        return this;
    }

    public Validator validate(String field, String value) {
        return validate(field, value, 0, null);
    }

    public Validator validate(String field, TextView value) {
        return validate(field, value.getText().toString(), 0, null);
    }

    public Validator validate(String field, String value, int param1) {
        return validate(field, value, param1, null);
    }

    public Validator validate(String field, TextView value, int param1) {
        return validate(field, value.getText().toString(), param1, null);
    }

    public Validator validate(String field, String value, String param2) {
        return validate(field, value, 0, param2);
    }

    public Validator validate(String field, TextView value, String param2) {
        return validate(field, value.getText().toString(), 0, param2);
    }

    public Validator validate(String field, TextView value, TextView param2) {
        return validate(field, value.getText().toString(), 0, param2.getText().toString());
    }

    public Validator validate(String field, TextView value, int param1, String param2) {
        return validate(field, value.getText().toString(), param1, param2);
    }

    public Validator validate(String field, String value, int param1, String param2) {
        _isValid = true;

        for (Rules rule : _rules) {
            switch (rule) {
                case MIN:
                    if (value.length() < param1) {
                        if (_showToast) {
                            _messages.add("The " + field + " must be at least " + param1 + " characters.");
                        }
                        _isValid = false;
                    }

                    break;

                case MAX:
                    if (value.length() > param1) {
                        if (_showToast) {
                            _messages.add("The " + field + " may not be greater than " + param1 + " characters.");
                        }
                        _isValid = false;
                    }

                    break;

                case CONFIRMED:
                    if (!value.equals(param2)) {
                        if (_showToast) {
                            _messages.add("The " + field + " confirmation does not match.");
                        }
                        _isValid = false;
                    }

                    break;

                case EMAIL:
//                    EmailValidator emailValidator = new EmailValidator();
//                    if (!emailValidator.validateEmail(value)) {
//                        if (_showToast) {
//                            _messages.add("The " + field + " must be a valid email address.");
//                        }
//                        _isValid = false;
//                    }

                    break;

                case NUMERIC:
                    if (!value.matches("[0-9]+")) {
                        if (_showToast) {
                            _messages.add("The " + field + " must be a number.");
                        }
                        _isValid = false;
                    }

                    break;

                case REQUIRED:
                    if (value == null || value.isEmpty()) {
                        if (_showToast) {
                            _messages.add("The " + field + " field is required.");
                        }
                        _isValid = false;
                    }

                    break;
            }

            if (!_isValid) {
                break; // Stop on first error!
            }
        }

        return this;
    }

    public boolean isValid() {
        return _isValid;
    }

    public boolean fails() {
        String message = "";
        for (String line : _messages) {
            message += (message.isEmpty() ? line : "\r\n" + line);
        }

        if (!message.isEmpty()) {
            Toast.makeText(_context, message, Toast.LENGTH_LONG).show();
        }

        return !_isValid;
    }

    public Validator clear() {
        _messages.clear();

        return this;
    }
}
