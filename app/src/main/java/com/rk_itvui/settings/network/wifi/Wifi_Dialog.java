package com.rk_itvui.settings.network.wifi;

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.NetworkInfo.DetailedState;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.IpConfiguration;
//import android.net.IpConfiguration.IpAssignment;
//import android.net.IpConfiguration.ProxySettings;
import android.net.LinkAddress;
import android.net.wifi.WifiConfiguration.Status;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Eap;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiManager;
import android.net.ProxyInfo;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.security.Credentials;
import android.security.KeyStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.net.InetAddress;
import android.util.Log;
import android.net.LinkProperties;
import java.util.Iterator;
import android.net.LinkAddress;
import android.net.RouteInfo;
import android.widget.Button;
import android.net.NetworkUtils;
import android.view.LayoutInflater;
import android.text.TextUtils;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Iterator;


import com.rk_itvui.settings.ProxySelector;
import com.zxy.idersettings.R;
import com.rk_itvui.settings.network.wifi.AccessPoint;
import com.rk_itvui.settings.network.wifi.Summary;

@SuppressLint("NewApi")
public class Wifi_Dialog extends AlertDialog implements View.OnClickListener,
		TextWatcher, AdapterView.OnItemSelectedListener {
	private static final String KEYSTORE_SPACE = "keystore://";

	private String unspecifiedCert = "unspecified";
	private static final int unspecifiedCertIndex = 0;

	/* Phase2 methods supported by PEAP are limited */
	private final ArrayAdapter<String> PHASE2_PEAP_ADAPTER;
	/* Full list of phase2 methods */
	private final ArrayAdapter<String> PHASE2_FULL_ADAPTER;

	public static final int BUTTON_SUBMIT = DialogInterface.BUTTON_POSITIVE;
	public static final int BUTTON_FORGET = DialogInterface.BUTTON_NEUTRAL;
	/* This value comes from "wifi_ip_settings" resource array */
	private static final int DHCP = 0;
	private static final int STATIC_IP = 1;

	/* These values come from "wifi_network_setup" resource array */
	public static final int MANUAL = 0;
	public static final int WPS_PBC = 1;
	public static final int WPS_KEYPAD = 2;
	public static final int WPS_DISPLAY = 3;

	/* These values come from "wifi_proxy_settings" resource array */
	public static final int PROXY_NONE = 0;
	public static final int PROXY_STATIC = 1;

	public final boolean edit;
	public final boolean mEdit;
	private final DialogInterface.OnClickListener mListener;
	private final AccessPoint mAccessPoint;

	private View mView;
	private TextView mSsid;
	private int mSecurity;
	// e.g. AccessPoint.SECURITY_NONE
	private int mAccessPointSecurity;
	private TextView mPasswordView;

	private Spinner mSecuritySpinner;
	private Spinner mEapMethodSpinner;
	private Spinner mEapCaCertSpinner;
	private Spinner mPhase2Spinner;
	private Spinner mEapUserCertSpinner;

	private Spinner mEapMethod;
	private Spinner mEapCaCert;
	private Spinner mPhase2;
	private Spinner mEapUserCert;
	private TextView mEapIdentity;
	private TextView mEapAnonymous;
	private Context mContext;

	// Associated with mPhase2Spinner, one of PHASE2_FULL_ADAPTER or
	// PHASE2_PEAP_ADAPTER
	private ArrayAdapter<String> mPhase2Adapter;
	private TextView mEapIdentityView;
	private TextView mEapAnonymousView;

	/* These values come from "wifi_eap_method" resource array */
	public static final int WIFI_EAP_METHOD_PEAP = 0;
	public static final int WIFI_EAP_METHOD_TLS = 1;
	public static final int WIFI_EAP_METHOD_TTLS = 2;
	public static final int WIFI_EAP_METHOD_PWD = 3;

	/* These values come from "wifi_peap_phase2_entries" resource array */
	public static final int WIFI_PEAP_PHASE2_NONE = 0;
	public static final int WIFI_PEAP_PHASE2_MSCHAPV2 = 1;
	public static final int WIFI_PEAP_PHASE2_GTC = 2;
	public static final int INVALID_NETWORK_ID = -1;

	private static final String TAG = "WifiConfigController";

	private Spinner mNetworkSetupSpinner;
	private Spinner mIpSettingsSpinner;
	private TextView mIpAddressView;
	private TextView mGatewayView;
	private TextView mNetworkPrefixLengthView;
	private TextView mDns1View;
	private TextView mDns2View;

	private Spinner mProxySettingsSpinner;
	private TextView mProxyHostView;
	private TextView mProxyPortView;
	private TextView mProxyExclusionListView;

	//private LinkProperties mLinkProperties = new LinkProperties();
    private LinkProperties mLinkProperties = null;
//	private IpAssignment mIpAssignment = IpAssignment.UNASSIGNED;
//	private ProxySettings mProxySettings = ProxySettings.UNASSIGNED;

    	private ProxyInfo mHttpProxy = null;
        private StaticIpConfiguration mStaticIpConfiguration = null;

	public Wifi_Dialog(Context context,
			DialogInterface.OnClickListener listener, AccessPoint accessPoint,
			boolean edit) {
		super(context);
		mContext = context;
		this.edit = edit;
		mEdit = edit;
		mListener = listener;
		mAccessPoint = accessPoint;
		mSecurity = (accessPoint == null) ? AccessPoint.SECURITY_NONE
				: accessPoint.security;

		PHASE2_PEAP_ADAPTER = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, context.getResources()
						.getStringArray(R.array.wifi_peap_phase2_entries));
		PHASE2_PEAP_ADAPTER
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		PHASE2_FULL_ADAPTER = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, context.getResources()
						.getStringArray(R.array.wifi_phase2_entries));
		PHASE2_FULL_ADAPTER
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		unspecifiedCert = context.getString(R.string.wifi_unspecified);
	}

	public boolean isEdit() {
		return mEdit;
	}


    WifiConfiguration getConfig() {
        if (mAccessPoint != null && mAccessPoint.networkId != INVALID_NETWORK_ID && !mEdit) {
            return null;
        }

        WifiConfiguration config = new WifiConfiguration();

        if (mAccessPoint == null) {
            config.SSID = AccessPoint.convertToQuotedString(
                    mSsid.getText().toString());
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else if (mAccessPoint.networkId == INVALID_NETWORK_ID) {
            config.SSID = AccessPoint.convertToQuotedString(
                    mAccessPoint.ssid);
        } else {
            config.networkId = mAccessPoint.networkId;
        }

        switch (mSecurity) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;

            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (mPasswordView.length() != 0) {
                    int length = mPasswordView.length();
                    String password = mPasswordView.getText().toString();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) &&
                            password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;

            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (mPasswordView.length() != 0) {
                    String password = mPasswordView.getText().toString();
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

            case AccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                config.enterpriseConfig = new WifiEnterpriseConfig();
                int eapMethod = mEapMethodSpinner.getSelectedItemPosition();
                int phase2Method = mPhase2Spinner.getSelectedItemPosition();
                config.enterpriseConfig.setEapMethod(eapMethod);
                switch (eapMethod) {
                    case Eap.PEAP:
                        // PEAP supports limited phase2 values
                        // Map the index from the PHASE2_PEAP_ADAPTER to the one used
                        // by the API which has the full list of PEAP methods.
                        switch(phase2Method) {
                            case WIFI_PEAP_PHASE2_NONE:
                                config.enterpriseConfig.setPhase2Method(Phase2.NONE);
                                break;
                            case WIFI_PEAP_PHASE2_MSCHAPV2:
                                config.enterpriseConfig.setPhase2Method(Phase2.MSCHAPV2);
                                break;
                            case WIFI_PEAP_PHASE2_GTC:
                                config.enterpriseConfig.setPhase2Method(Phase2.GTC);
                                break;
                            default:
                                Log.e(TAG, "Unknown phase2 method" + phase2Method);
                                break;
                        }
                        break;
                    default:
                        // The default index from PHASE2_FULL_ADAPTER maps to the API
                        config.enterpriseConfig.setPhase2Method(phase2Method);
                        break;
                }
                String caCert = (String) mEapCaCertSpinner.getSelectedItem();
                if (caCert.equals(unspecifiedCert)) caCert = "";
                //config.enterpriseConfig.setCaCertificateAlias(caCert);
                String clientCert = (String) mEapUserCertSpinner.getSelectedItem();
                if (clientCert.equals(unspecifiedCert)) clientCert = "";
                //config.enterpriseConfig.setClientCertificateAlias(clientCert);
                config.enterpriseConfig.setIdentity(mEapIdentityView.getText().toString());
                config.enterpriseConfig.setAnonymousIdentity(
                        mEapAnonymousView.getText().toString());

                if (mPasswordView.isShown()) {
                    // For security reasons, a previous password is not displayed to user.
                    // Update only if it has been changed.
                    if (mPasswordView.length() > 0) {
                        config.enterpriseConfig.setPassword(mPasswordView.getText().toString());
                    }
                } else {
                    // clear password
                    config.enterpriseConfig.setPassword(mPasswordView.getText().toString());
                }
                break;
            default:
                return null;
        }

//        config.setIpConfiguration(
//                new IpConfiguration(mIpAssignment, mProxySettings,
//                                    mStaticIpConfiguration, mHttpProxy));

        return config;
    }
  
    public  String intToIp(int i) {  
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)  
                + "." + (i >> 24 & 0xFF);  
    }  
    
    public  String getIp(Context contxext) {  
        WifiManager wm = (WifiManager) contxext.getSystemService(Context.WIFI_SERVICE);  

        if (!wm.isWifiEnabled())  
            wm.setWifiEnabled(true);  
        WifiInfo wi = wm.getConnectionInfo();  
        int ipAdd = wi.getIpAddress();  
        String ip = intToIp(ipAdd);  
        return ip;  
    }  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mView = getLayoutInflater().inflate(R.layout.wifi_dialog, null);
		setView(mView);
		setInverseBackgroundForced(true);
		Context context = getContext();
		Resources resources = context.getResources();

		if (mAccessPoint == null) { //new network
			setTitle(R.string.wifi_add_network);

			mSsid = (TextView) mView.findViewById(R.id.ssid);
			mSsid.addTextChangedListener(this);
			mSecuritySpinner = ((Spinner) mView.findViewById(R.id.security));
			mSecuritySpinner.setOnItemSelectedListener(this);
			if (false) {
				mView.findViewById(R.id.type_ssid).setVisibility(View.VISIBLE);
				mView.findViewById(R.id.type_security).setVisibility(
						View.VISIBLE);
				// We want custom layout. The content must be same as the
				// other cases.
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.wifi_setup_custom_list_item_1,
						android.R.id.text1, context.getResources()
								.getStringArray(R.array.wifi_security_no_eap));
				mSecuritySpinner.setAdapter(adapter);
			} else {
				mView.findViewById(R.id.type).setVisibility(View.VISIBLE);
			}
			setButton(BUTTON_SUBMIT, context.getString(R.string.wifi_save),
					mListener);
		} else {
			setTitle(mAccessPoint.ssid);

			mIpSettingsSpinner = (Spinner) mView.findViewById(R.id.ip_settings);
			mIpSettingsSpinner.setOnItemSelectedListener(this);
			mProxySettingsSpinner = (Spinner) mView
					.findViewById(R.id.proxy_settings);
			mProxySettingsSpinner.setOnItemSelectedListener(this);

			ViewGroup group = (ViewGroup) mView.findViewById(R.id.info);

			DetailedState state = mAccessPoint.getState();
			if (state != null) {
				addRow(group, R.string.wifi_status,
						Summary.get(getContext(), state));
			}

			int level = mAccessPoint.getLevel();
			if (level != -1) {
				String[] signal = resources.getStringArray(R.array.wifi_signal);
				addRow(group, R.string.wifi_signal, signal[level]);
			}

			WifiInfo info = mAccessPoint.getInfo();
			if (info != null && info.getLinkSpeed() != -1) {
				addRow(group, R.string.wifi_speed, info.getLinkSpeed()
						+ WifiInfo.LINK_SPEED_UNITS);
			}

			addRow(group, R.string.wifi_security,
					mAccessPoint.getSecurityString(false));

			boolean showAdvancedFields = false;
			if (mAccessPoint.networkId != -1) {
				WifiConfiguration config = mAccessPoint.getConfig();
				
//				if (config.getIpAssignment() == IpAssignment.STATIC) {
//					mIpSettingsSpinner.setSelection(1);
//					showAdvancedFields = true;
//				} else {
					mIpSettingsSpinner.setSelection(0);
//				}
				
				// Display IP addresses 
//                    		StaticIpConfiguration staticConfig = config.getStaticIpConfiguration();
//                    		if (staticConfig != null && staticConfig.ipAddress != null) {
                        		addRow(group, R.string.wifi_ip_address,getIp(context)
                          		 );
//                   		 }

//				if (config.getProxySettings() == ProxySettings.STATIC) {
//					mProxySettingsSpinner.setSelection(1);
//					showAdvancedFields = true;
//				} else if (config.getProxySettings() == ProxySettings.PAC) {
//					mProxySettingsSpinner.setVisibility(View.GONE);
//					TextView textView = (TextView) mView
//							.findViewById(R.id.proxy_pac_info);
//					textView.setVisibility(View.VISIBLE);
//
//
//					textView.setText(context.getString(R.string.proxy_url)
//							+ config.getHttpProxy()
//									.getPacFileUrl());
//					showAdvancedFields = true;
//				} else {
					mProxySettingsSpinner.setSelection(0);
//				}

				if (config.status == Status.DISABLED){
						//&& config.disableReason == WifiConfiguration.DISABLED_DNS_FAILURE) {
					addRow(group, R.string.wifi_disabled_heading,
							context.getString(R.string.wifi_disabled_help));
				}

			}

			// Show network setup options only for a new network
			if (mAccessPoint.networkId == -1 && mAccessPoint.mWPS_enabled) {
				showNetworkSetupFields();
			}

			if (mAccessPoint.networkId == -1 || mEdit) {
				showSecurityFields();
				showIpConfigFields();
				showProxyFields();
				mView.findViewById(R.id.wifi_advanced_toggle).setVisibility(
						View.VISIBLE);
				mView.findViewById(R.id.wifi_advanced_togglebox)
						.setOnClickListener(this);
				if (showAdvancedFields) {
					((CheckBox) mView
							.findViewById(R.id.wifi_advanced_togglebox))
							.setChecked(true);
					mView.findViewById(R.id.wifi_advanced_fields)
							.setVisibility(View.VISIBLE);
				}
			}

			if (mEdit) {
				setButton(BUTTON_SUBMIT, context.getString(R.string.wifi_save),
						mListener);
			} else {
				if (state == null && level != -1) {
					setButton(BUTTON_SUBMIT,
							context.getString(R.string.wifi_connect), mListener);
				} else {
					mView.findViewById(R.id.ip_fields).setVisibility(View.GONE);
				}
				if (mAccessPoint.networkId != -1) {
					setButton(BUTTON_FORGET,
							context.getString(R.string.wifi_forget), mListener);
				}
			}
		}

		setButton(BUTTON_NEGATIVE, context.getString(R.string.wifi_cancel),
				mListener);
		if (getButton(BUTTON_SUBMIT) != null) {
			enableSubmitIfAppropriate();
		}
		super.onCreate(savedInstanceState);

	}

	private void addRow(ViewGroup group, int name, String value) {
		View row = getLayoutInflater().inflate(R.layout.wifi_dialog_row, group,
				false);
		((TextView) row.findViewById(R.id.name)).setText(name);
		((TextView) row.findViewById(R.id.value)).setText(value);
		group.addView(row);
	}

	private void validate() {
		// TODO: make sure this is complete.
		if ((mSsid != null && mSsid.length() == 0)
				|| ((mAccessPoint == null || mAccessPoint.networkId == -1) && ((mSecurity == AccessPoint.SECURITY_WEP && mPasswordView
						.length() == 0) || (mSecurity == AccessPoint.SECURITY_PSK && mPasswordView
						.length() < 8)))) {
			getButton(BUTTON_SUBMIT).setEnabled(false);
		} else {
			getButton(BUTTON_SUBMIT).setEnabled(true);
		}
	}

	public void onClick(View view) {
		Log.i("wifi_dialog", "view id is " + view.getId());
		if (view.getId() == R.id.show_password) {
			mPasswordView
					.setInputType(InputType.TYPE_CLASS_TEXT
							| (((CheckBox) view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
									: InputType.TYPE_TEXT_VARIATION_PASSWORD));
		} else if (view.getId() == R.id.wifi_advanced_togglebox) {
			if (((CheckBox) view).isChecked()) {
				mView.findViewById(R.id.wifi_advanced_fields).setVisibility(
						View.VISIBLE);
			} else {
				mView.findViewById(R.id.wifi_advanced_fields).setVisibility(
						View.GONE);
			}
		}/*
		 * mPassword.setInputType( InputType.TYPE_CLASS_TEXT | (((CheckBox)
		 * view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
		 * InputType.TYPE_TEXT_VARIATION_PASSWORD));
		 */
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	public void afterTextChanged(Editable editable) {
		// validate();
		enableSubmitIfAppropriate();
	}

	public void onItemSelected(AdapterView parent, View view, int position,
			long id) {
		// showSecurityFields();
		// validate();
		if (parent == mSecuritySpinner) {
			mSecurity = position;
			showSecurityFields();
		} else if (parent == mNetworkSetupSpinner) {
			showNetworkSetupFields();
		} else if (parent == mProxySettingsSpinner) {
			showProxyFields();
		} else {
			showIpConfigFields();
		}
		enableSubmitIfAppropriate();
	}

	public void onNothingSelected(AdapterView parent) {
	}

	@SuppressLint("NewApi")
	private void showSecurityFields() {
		if (mSecurity == AccessPoint.SECURITY_NONE) {
			mView.findViewById(R.id.security_fields).setVisibility(View.GONE);
			return;
		}
		mView.findViewById(R.id.security_fields).setVisibility(View.VISIBLE);

		if (mPasswordView == null) {
			mPasswordView = (TextView) mView.findViewById(R.id.password);
			mPasswordView.addTextChangedListener(this);
			((CheckBox) mView.findViewById(R.id.show_password))
					.setOnClickListener(this);

			if (mAccessPoint != null && mAccessPoint.networkId != -1) {
				mPasswordView.setHint(R.string.wifi_unchanged);
			}
		}

		if (mSecurity != AccessPoint.SECURITY_EAP) {
			mView.findViewById(R.id.eap).setVisibility(View.GONE);
			return;
		}
		mView.findViewById(R.id.eap).setVisibility(View.VISIBLE);

		if (mEapMethod == null) {
			mEapMethod = (Spinner) mView.findViewById(R.id.method);
			mPhase2 = (Spinner) mView.findViewById(R.id.phase2);
			mEapCaCert = (Spinner) mView.findViewById(R.id.ca_cert);
			mEapUserCert = (Spinner) mView.findViewById(R.id.user_cert);
			mEapIdentity = (TextView) mView.findViewById(R.id.identity);
			mEapAnonymous = (TextView) mView.findViewById(R.id.anonymous);

			loadCertificates(mEapCaCert, Credentials.CA_CERTIFICATE);
			loadCertificates(mEapUserCert, Credentials.USER_PRIVATE_KEY);

			if (mAccessPoint != null && mAccessPoint.networkId != -1) {
				WifiEnterpriseConfig enterpriseConfig = mAccessPoint
						.getConfig().enterpriseConfig;
				int eapMethod = enterpriseConfig.getEapMethod();
				int phase2Method = enterpriseConfig.getPhase2Method();
				mEapMethodSpinner.setSelection(eapMethod);
				showEapFieldsByMethod(eapMethod);
				switch (eapMethod) {
				case Eap.PEAP:
					switch (phase2Method) {
					case Phase2.NONE:
						mPhase2Spinner.setSelection(WIFI_PEAP_PHASE2_NONE);
						break;
					case Phase2.MSCHAPV2:
						mPhase2Spinner.setSelection(WIFI_PEAP_PHASE2_MSCHAPV2);
						break;
					case Phase2.GTC:
						mPhase2Spinner.setSelection(WIFI_PEAP_PHASE2_GTC);
						break;
					default:
						Log.e(TAG, "Invalid phase 2 method " + phase2Method);
						break;
					}
					break;
				default:
					mPhase2Spinner.setSelection(phase2Method);
					break;
				}
				/*setSelection(mEapCaCertSpinner,
						enterpriseConfig.getCaCertificateAlias());
				setSelection(mEapUserCertSpinner,
						enterpriseConfig.getClientCertificateAlias());*/
				mEapIdentityView.setText(enterpriseConfig.getIdentity());
				mEapAnonymousView.setText(enterpriseConfig
						.getAnonymousIdentity());
			} else {
				// Choose a default for a new network and show only appropriate
				// fields
				mEapMethodSpinner.setSelection(Eap.PEAP);
				showEapFieldsByMethod(Eap.PEAP);
			}
		} else {
			showEapFieldsByMethod(mEapMethodSpinner.getSelectedItemPosition());
		}
	}

	private void showEapFieldsByMethod(int eapMethod) {
		// Common defaults
		mView.findViewById(R.id.l_method).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.l_identity).setVisibility(View.VISIBLE);

		// Defaults for most of the EAP methods and over-riden by
		// by certain EAP methods
		mView.findViewById(R.id.l_ca_cert).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.password_layout).setVisibility(View.VISIBLE);
		mView.findViewById(R.id.show_password_layout).setVisibility(
				View.VISIBLE);

		switch (eapMethod) {
		case WIFI_EAP_METHOD_PWD:
			setPhase2Invisible();
			setCaCertInvisible();
			setAnonymousIdentInvisible();
			setUserCertInvisible();
			break;
		case WIFI_EAP_METHOD_TLS:
			mView.findViewById(R.id.l_user_cert).setVisibility(View.VISIBLE);
			setPhase2Invisible();
			setAnonymousIdentInvisible();
			setPasswordInvisible();
			break;
		case WIFI_EAP_METHOD_PEAP:
			// Reset adapter if needed
			if (mPhase2Adapter != PHASE2_PEAP_ADAPTER) {
				mPhase2Adapter = PHASE2_PEAP_ADAPTER;
				mPhase2Spinner.setAdapter(mPhase2Adapter);
			}
			mView.findViewById(R.id.l_phase2).setVisibility(View.VISIBLE);
			mView.findViewById(R.id.l_anonymous).setVisibility(View.VISIBLE);
			setUserCertInvisible();
			break;
		case WIFI_EAP_METHOD_TTLS:
			// Reset adapter if needed
			if (mPhase2Adapter != PHASE2_FULL_ADAPTER) {
				mPhase2Adapter = PHASE2_FULL_ADAPTER;
				mPhase2Spinner.setAdapter(mPhase2Adapter);
			}
			mView.findViewById(R.id.l_phase2).setVisibility(View.VISIBLE);
			mView.findViewById(R.id.l_anonymous).setVisibility(View.VISIBLE);
			setUserCertInvisible();
			break;
		}
	}

	private void setPhase2Invisible() {
		mView.findViewById(R.id.l_phase2).setVisibility(View.GONE);
		mPhase2Spinner.setSelection(Phase2.NONE);
	}

	private void setCaCertInvisible() {
		mView.findViewById(R.id.l_ca_cert).setVisibility(View.GONE);
		mEapCaCertSpinner.setSelection(unspecifiedCertIndex);
	}

	private void setUserCertInvisible() {
		mView.findViewById(R.id.l_user_cert).setVisibility(View.GONE);
		mEapUserCertSpinner.setSelection(unspecifiedCertIndex);
	}

	private void setAnonymousIdentInvisible() {
		mView.findViewById(R.id.l_anonymous).setVisibility(View.GONE);
		mEapAnonymousView.setText("");
	}

	private void setPasswordInvisible() {
		mPasswordView.setText("");
		mView.findViewById(R.id.password_layout).setVisibility(View.GONE);
		mView.findViewById(R.id.show_password_layout).setVisibility(View.GONE);
	}

	private void loadCertificates(Spinner spinner, String prefix) {
		String[] certs = null;
				/*KeyStore.getInstance().saw(prefix,
				//android.os.Process.WIFI_UID);
                1010);*/
		Context context = getContext();
		String unspecified = context.getString(R.string.wifi_unspecified);

		if (certs == null || certs.length == 0) {
			certs = new String[] { unspecifiedCert };
		} else {
			String[] array = new String[certs.length + 1];
			array[0] = unspecifiedCert;
			System.arraycopy(certs, 0, array, 1, certs.length);
			certs = array;
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, certs);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	private void setCertificate(Spinner spinner, String prefix, String cert) {
		prefix = KEYSTORE_SPACE + prefix;
		if (cert != null && cert.startsWith(prefix)) {
			setSelection(spinner, cert.substring(prefix.length()));
		}
	}

	private void setSelection(Spinner spinner, String value) {
		if (value != null) {
			ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner
					.getAdapter();
			for (int i = adapter.getCount() - 1; i >= 0; --i) {
				if (value.equals(adapter.getItem(i))) {
					spinner.setSelection(i);
					break;
				}
			}
		}
	}

    private void showIpConfigFields() {
        WifiConfiguration config = null;

        mView.findViewById(R.id.ip_fields).setVisibility(View.VISIBLE);

        if (mAccessPoint != null && mAccessPoint.networkId != INVALID_NETWORK_ID) {
            config = mAccessPoint.getConfig();
        }

        if (mIpSettingsSpinner.getSelectedItemPosition() == STATIC_IP) {
            mView.findViewById(R.id.staticip).setVisibility(View.VISIBLE);
            if (mIpAddressView == null) {
                mIpAddressView = (TextView) mView.findViewById(R.id.ipaddress);
                mIpAddressView.addTextChangedListener(this);
                mGatewayView = (TextView) mView.findViewById(R.id.gateway);
                mGatewayView.addTextChangedListener(this);
                mNetworkPrefixLengthView = (TextView) mView.findViewById(
                        R.id.network_prefix_length);
                mNetworkPrefixLengthView.addTextChangedListener(this);
                mDns1View = (TextView) mView.findViewById(R.id.dns1);
                mDns1View.addTextChangedListener(this);
                mDns2View = (TextView) mView.findViewById(R.id.dns2);
                mDns2View.addTextChangedListener(this);
            }
            if (config != null) {
                //StaticIpConfiguration staticConfig = config.getStaticIpConfiguration();
                StaticIpConfiguration staticConfig = null ;
                if (staticConfig != null) {
                    if (staticConfig.ipAddress != null) {
                        mIpAddressView.setText(
                                staticConfig.ipAddress.getAddress().getHostAddress());
                        //mNetworkPrefixLengthView.setText(Integer.toString(staticConfig.ipAddress
                          //      .getNetworkPrefixLength()));
                        mNetworkPrefixLengthView.setText("test12");
                    }

                    if (staticConfig.gateway != null) {
                        mGatewayView.setText(staticConfig.gateway.getHostAddress());
                    }

                    Iterator<InetAddress> dnsIterator = staticConfig.dnsServers.iterator();
                    if (dnsIterator.hasNext()) {
                        mDns1View.setText(dnsIterator.next().getHostAddress());
                    }
                    if (dnsIterator.hasNext()) {
                        mDns2View.setText(dnsIterator.next().getHostAddress());
                    }
                }
            }
        } else {
            mView.findViewById(R.id.staticip).setVisibility(View.GONE);
        }
    }

	private void showProxyFields() {
		WifiConfiguration config = null;

		mView.findViewById(R.id.proxy_settings_fields).setVisibility(
				View.VISIBLE);

		if (mAccessPoint != null && mAccessPoint.networkId != -1) {
			config = mAccessPoint.getConfig();
		}

		if (mProxySettingsSpinner.getSelectedItemPosition() == PROXY_STATIC) {
			mView.findViewById(R.id.proxy_warning_limited_support)
					.setVisibility(View.VISIBLE);
			mView.findViewById(R.id.proxy_fields).setVisibility(View.VISIBLE);
			if (mProxyHostView == null) {
				mProxyHostView = (TextView) mView
						.findViewById(R.id.proxy_hostname);
				mProxyHostView.addTextChangedListener(this);
				mProxyPortView = (TextView) mView.findViewById(R.id.proxy_port);
				mProxyPortView.addTextChangedListener(this);
				mProxyExclusionListView = (TextView) mView
						.findViewById(R.id.proxy_exclusionlist);
				mProxyExclusionListView.addTextChangedListener(this);
			}
			if (config != null) {
				//ProxyInfo proxyProperties = config.getHttpProxy();
                ProxyInfo proxyProperties = null;
				if (proxyProperties != null) {
					mProxyHostView.setText(proxyProperties.getHost());
					mProxyPortView.setText(Integer.toString(proxyProperties
							.getPort()));
					//mProxyExclusionListView.setText(proxyProperties.getExclusionListAsString());
				}
			}
		} else {
			mView.findViewById(R.id.proxy_warning_limited_support)
					.setVisibility(View.GONE);
			mView.findViewById(R.id.proxy_fields).setVisibility(View.GONE);
		}
	}

	private void enableSubmitIfAppropriate() {
		Button submit = getButton(BUTTON_SUBMIT);
		if (submit == null)
			return;
		boolean enabled = false;

		boolean passwordInvalid = false;

		/* Check password invalidity for manual network set up alone */
		if (chosenNetworkSetupMethod() == MANUAL
				&& ((mSecurity == AccessPoint.SECURITY_WEP && mPasswordView
						.length() == 0) || (mSecurity == AccessPoint.SECURITY_PSK && mPasswordView
						.length() < 8))) {
			passwordInvalid = true;
		}

		if ((mSsid != null && mSsid.length() == 0)
				|| ((mAccessPoint == null || mAccessPoint.networkId == -1) && passwordInvalid)) {
			enabled = false;
		} else {
			if (ipAndProxyFieldsAreValid()) {
				enabled = true;
			} else {
				enabled = false;
			}
		}
		submit.setEnabled(enabled);
	}

	private boolean ipAndProxyFieldsAreValid() {
		//mLinkProperties.clear();
//		mIpAssignment = (mIpSettingsSpinner != null && mIpSettingsSpinner
//				.getSelectedItemPosition() == STATIC_IP) ? IpAssignment.STATIC
//				: IpAssignment.DHCP;
//
//		if (mIpAssignment == IpAssignment.STATIC) {
//                       mStaticIpConfiguration = new StaticIpConfiguration();
//			int result = validateIpConfigFields_new(mStaticIpConfiguration);
//			if (result != 0) {
//				return false;
//			}
//		}

//		mProxySettings = (mProxySettingsSpinner != null && mProxySettingsSpinner
//				.getSelectedItemPosition() == PROXY_STATIC) ? ProxySettings.STATIC
//				: ProxySettings.NONE;
//
//		if (mProxySettings == ProxySettings.STATIC) {
//			String host = mProxyHostView.getText().toString();
//			String portStr = mProxyPortView.getText().toString();
//			String exclusionList = mProxyExclusionListView.getText().toString();
//			int port = 0;
//			int result = 0;
//			try {
//				port = Integer.parseInt(portStr);
//				result = ProxySelector.validate(host, portStr, exclusionList);
//			} catch (NumberFormatException e) {
//				result = R.string.proxy_error_invalid_port;
//			}
//			if (result == 0) {
//				ProxyInfo proxyProperties = new ProxyInfo(host,
//						port, exclusionList);
//				mLinkProperties.setHttpProxy(proxyProperties);
//			} else {
//				return false;
//			}
//		}
		return true;
	}

	private void showNetworkSetupFields() {
		mView.findViewById(R.id.setup_fields).setVisibility(View.VISIBLE);

		if (mNetworkSetupSpinner == null) {
			mNetworkSetupSpinner = (Spinner) mView
					.findViewById(R.id.network_setup);
			mNetworkSetupSpinner.setOnItemSelectedListener(this);
		}

		int pos = mNetworkSetupSpinner.getSelectedItemPosition();

		/* Show pin text input if needed */
		if (pos == WPS_KEYPAD) {
			mView.findViewById(R.id.wps_fields).setVisibility(View.VISIBLE);
		} else {
			mView.findViewById(R.id.wps_fields).setVisibility(View.GONE);
		}

		/* show/hide manual security fields appropriately */
		if ((pos == WPS_DISPLAY) || (pos == WPS_KEYPAD) || (pos == WPS_PBC)) {
			mView.findViewById(R.id.security_fields).setVisibility(View.GONE);
		} else {
			mView.findViewById(R.id.security_fields)
					.setVisibility(View.VISIBLE);
		}

	}

	int chosenNetworkSetupMethod() {
		if (mNetworkSetupSpinner != null) {
			return mNetworkSetupSpinner.getSelectedItemPosition();
		}
		return MANUAL;
	}
    private Inet4Address getIPv4Address(String text) {
        try {
            return (Inet4Address) NetworkUtils.numericToInetAddress(text);
        } catch (Exception e) {
            return null;
        }
    }

    private int validateIpConfigFields_new(StaticIpConfiguration staticIpConfiguration) {
        if (mIpAddressView == null) return 0;

        String ipAddr = mIpAddressView.getText().toString();
        if (TextUtils.isEmpty(ipAddr)) return R.string.wifi_ip_settings_invalid_ip_address;

        Inet4Address inetAddr = getIPv4Address(ipAddr);
        if (inetAddr == null) {
            return R.string.wifi_ip_settings_invalid_ip_address;
        }

        int networkPrefixLength = -1;
        try {
            networkPrefixLength = Integer.parseInt(mNetworkPrefixLengthView.getText().toString());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                return R.string.wifi_ip_settings_invalid_network_prefix_length;
            }
            staticIpConfiguration.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
            Log.d("blb", "validateIpConfigFields_new" + e); 
        }

        String gateway = mGatewayView.getText().toString();
        if (TextUtils.isEmpty(gateway)) {
            try {
                //Extract a default gateway from IP address
                InetAddress netPart = NetworkUtils.getNetworkPart(inetAddr, networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length-1] = 1;
                mGatewayView.setText(InetAddress.getByAddress(addr).getHostAddress());
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = getIPv4Address(gateway);
            if (gatewayAddr == null) {
                return R.string.wifi_ip_settings_invalid_gateway;
            }
            staticIpConfiguration.gateway = gatewayAddr;
        }

        String dns = mDns1View.getText().toString();
        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            //If everything else is valid, provide hint as a default option
           // mDns1View.setText(mConfigUi.getContext().getString(R.string.wifi_dns1_hint));
            Log.d("blb","no dns server");        
        } else {
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }

        if (mDns2View.length() > 0) {
            dns = mDns2View.getText().toString();
            dnsAddr = getIPv4Address(dns);
            if (dnsAddr == null) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            staticIpConfiguration.dnsServers.add(dnsAddr);
        }
        return 0;
    }
/*
	private int validateIpConfigFields(LinkProperties linkProperties) {
		String ipAddr = mIpAddressView.getText().toString();
		InetAddress inetAddr = null;
		try {
			inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
		} catch (IllegalArgumentException e) {
			return R.string.wifi_ip_settings_invalid_ip_address;
		}

		int networkPrefixLength = -1;
		try {
			networkPrefixLength = Integer.parseInt(mNetworkPrefixLengthView
					.getText().toString());
		} catch (NumberFormatException e) {
			// Use -1
		}
		if (networkPrefixLength < 0 || networkPrefixLength > 32) {
			return R.string.wifi_ip_settings_invalid_network_prefix_length;
		}
		linkProperties.addLinkAddress(new LinkAddress(inetAddr,
				networkPrefixLength));
		String gateway = mGatewayView.getText().toString();
		InetAddress gatewayAddr = null;
		try {
			gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
		} catch (IllegalArgumentException e) {
			return R.string.wifi_ip_settings_invalid_gateway;
		}
		linkProperties.addRoute(new RouteInfo(gatewayAddr));

		String dns = mDns1View.getText().toString();
		InetAddress dnsAddr = null;
		try {
			dnsAddr = NetworkUtils.numericToInetAddress(dns);
		} catch (IllegalArgumentException e) {
			return R.string.wifi_ip_settings_invalid_dns;
		}
		linkProperties.addDns(dnsAddr);
		if (mDns2View.length() > 0) {
			dns = mDns2View.getText().toString();
			try {
				dnsAddr = NetworkUtils.numericToInetAddress(dns);
			} catch (IllegalArgumentException e) {
				return R.string.wifi_ip_settings_invalid_dns;
			}
			linkProperties.addDns(dnsAddr);
		}
		return 0;
	}
*/
	/**
	 * Make the characters of the password visible if show_password is checked.
	 */
	private void updatePasswordVisibility(boolean checked) {
		int pos = mPasswordView.getSelectionEnd();
		mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT
				| (checked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
						: InputType.TYPE_TEXT_VARIATION_PASSWORD));
		if (pos >= 0) {
			((EditText) mPasswordView).setSelection(pos);
		}
	}

}
