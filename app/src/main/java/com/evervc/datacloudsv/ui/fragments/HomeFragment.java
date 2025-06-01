package com.evervc.datacloudsv.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evervc.datacloudsv.R;
import com.evervc.datacloudsv.ui.NewRegisterActivity;
import com.evervc.datacloudsv.ui.utils.AccountRegisterControllerDB;
import com.evervc.datacloudsv.ui.utils.ActivityTransitionUtil;
import com.evervc.datacloudsv.ui.utils.SortOption;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {
    private FloatingActionButton fabAddRegister;
    private RecyclerView rcvAccountRegisters;
    private TextView tvInformationMessage;
    private ActivityResultLauncher<Intent> newAccountRegisterLauncher;
    private MaterialToolbar toolbar;
    private Dialog sort_dialog;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Associating graphic elements
        bindElementsXml(view);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.navigation_filters_top, menu);
                MenuItem searchItem = menu.findItem(R.id.btnSearchBytitle);

                SearchView searchView = (SearchView) searchItem.getActionView();
                searchView.setQueryHint("Buscar por título...");

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        AccountRegisterControllerDB.showAccountRegistersByTitle(
                                query,
                                rcvAccountRegisters,
                                tvInformationMessage,
                                getContext(),
                                getChildFragmentManager(),
                                HomeFragment.this::onChangeAccountRegistersList,
                                newAccountRegisterLauncher
                        );
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        AccountRegisterControllerDB.showAccountRegistersByTitle(
                                newText,
                                rcvAccountRegisters,
                                tvInformationMessage,
                                getContext(),
                                getChildFragmentManager(),
                                HomeFragment.this::onChangeAccountRegistersList,
                                newAccountRegisterLauncher
                        );
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.STARTED);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*switch (item.getItemId()) {
                    case R.id.btnSortNewer:
                        return true;
                    case R.id.btnSortOlder:
                        return true;
                    case R.id.btnSortAsc:
                        return true;
                    case R.id.btnSortDesc:
                        return true;
                }*/
                if (item.getItemId() == R.id.btnSortRegisters) {
                    sortRegisters();
                }

                return false;
            }
        });


        newAccountRegisterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        onChangeAccountRegistersList();
                    }
                }
        );

        // Listener to add new register
        fabAddRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show activity for create a new register
                Intent newAccountRegisterIntent = new Intent(getContext(), NewRegisterActivity.class);
                newAccountRegisterLauncher.launch(newAccountRegisterIntent);
                ActivityTransitionUtil.applyEnterTransition(requireActivity());
            }
        });

        onChangeAccountRegistersList();

        return view;
    }

    private void bindElementsXml(View view) {
        fabAddRegister = view.findViewById(R.id.fabAddRegister);
        rcvAccountRegisters = view.findViewById(R.id.rcvAccountRegisters);
        tvInformationMessage = view.findViewById(R.id.tvInformationMessage);
        toolbar = view.findViewById(R.id.mtbFilters);
        sort_dialog = new Dialog(getActivity());
    }

    private void sortRegisters() {
        // Bind elements of Dialog
        Button btnSortNewer, btnSortOlder, btnSortAsc, btnSortDesc;

        sort_dialog.setContentView(R.layout.sort_registers_dialog);

        btnSortNewer = sort_dialog.findViewById(R.id.btnSortNewer);
        btnSortOlder = sort_dialog.findViewById(R.id.btnSortOlder);
        btnSortAsc = sort_dialog.findViewById(R.id.btnSortAsc);
        btnSortDesc = sort_dialog.findViewById(R.id.btnSortDesc);

        // Add events to buttons
        btnSortNewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountRegisterControllerDB.showAccountRegistersSorted(SortOption.NEWEST_FIRST, rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), HomeFragment.this::onChangeAccountRegistersList, newAccountRegisterLauncher);
                Toast.makeText(getActivity(), "Ordenando por más nuevos.", Toast.LENGTH_SHORT).show();
                sort_dialog.dismiss();
            }
        });

        btnSortOlder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountRegisterControllerDB.showAccountRegistersSorted(SortOption.OLDEST_FIRST, rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), HomeFragment.this::onChangeAccountRegistersList, newAccountRegisterLauncher);
                Toast.makeText(getActivity(), "Ordenando por más antiguos.", Toast.LENGTH_SHORT).show();
                sort_dialog.dismiss();
            }
        });

        btnSortAsc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountRegisterControllerDB.showAccountRegistersSorted(SortOption.TITLE_ASC, rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), HomeFragment.this::onChangeAccountRegistersList, newAccountRegisterLauncher);
                Toast.makeText(getActivity(), "Ordenando por titulo A-Z.", Toast.LENGTH_SHORT).show();
                sort_dialog.dismiss();
            }
        });

        btnSortDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountRegisterControllerDB.showAccountRegistersSorted(SortOption.TITLE_DESC, rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), HomeFragment.this::onChangeAccountRegistersList, newAccountRegisterLauncher);
                Toast.makeText(getActivity(), "Ordenando por titulo Z-A.", Toast.LENGTH_SHORT).show();
                sort_dialog.dismiss();
            }
        });

        //
        sort_dialog.show();
        sort_dialog.setCancelable(true);
    }

    private void onChangeAccountRegistersList() {
        AccountRegisterControllerDB.showAccountRegisters(rcvAccountRegisters, tvInformationMessage, getContext(), getChildFragmentManager(), this::onChangeAccountRegistersList, newAccountRegisterLauncher);
    }
}