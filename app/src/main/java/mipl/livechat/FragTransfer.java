package mipl.livechat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FragTransfer extends Fragment {

    StringRequest stringRequest;

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<PojoChat> mUsersT;
    LinearLayout ivNoRecordTransfer;

    SharedPreferences sharedpreferences;
    String user, password, Website, Location, UserID, role_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_transfer, container, false);

        sharedpreferences = getActivity().getSharedPreferences(commonVariables.mypreference, Context.MODE_PRIVATE);
        user = sharedpreferences.getString(commonVariables.Name, "");
        password = sharedpreferences.getString(commonVariables.Pass, "");
        Website = sharedpreferences.getString(commonVariables.Website, "");
        Location = sharedpreferences.getString(commonVariables.Location, "");
        UserID = sharedpreferences.getString(commonVariables.UserID, "");
        role_id = sharedpreferences.getString(commonVariables.role_id, "");

        ivNoRecordTransfer = (LinearLayout) v.findViewById(R.id.ivNoRecordTransfer);

        mUsersT = new ArrayList<>();
        getChats();

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rvTransferList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                getChats();
            }
        }, 10000, 10000);

        return v;
    }

    public void getChats() {

        String path;
        if (role_id.equals("2"))
            path = "/index.php/restapi/chats?limit=1000&status=2";
        else
            path = "/index.php/restapi/chats?limit=1000&status=2&user_id=" + UserID;

//        stringRequest = new StringRequest(Request.Method.POST, Location + "/index.php/restapi/chats?limit=1000&status=2",
        stringRequest = new StringRequest(Request.Method.POST, Location + path,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        mUsersT.clear();
                        Log.i("response", response);

                        JSONObject reader = null;
                        try {
                            reader = new JSONObject(response);

                            JSONArray list = reader.getJSONArray("list");

                            for (int i = 0; i < list.length(); i++) {
                                JSONObject chatList = list.getJSONObject(i);

                                String status = chatList.getString("status");
                                if (status.equals("2")) {

                                    String id = chatList.getString("id");
                                    String nick = chatList.getString("nick");
                                    String user_id = chatList.getString("user_id");
                                    String ip = chatList.getString("ip");
                                    String dep_id = chatList.getString("dep_id");
                                    String email = chatList.getString("email");
                                    String country_name = chatList.getString("country_name");
                                    String phone = chatList.getString("phone");
                                    String last_msg_id = chatList.getString("last_msg_id");
                                    String city = chatList.getString("city");
                                    String browser = chatList.getString("uagent");
                                    String referrer = chatList.getString("referrer");
//                                    referrer = referrer.replaceAll("/", "");
                                    String hash = chatList.getString("hash");
                                    String time = chatList.getString("time");

                                    try {
                                        String[] separated = referrer.split("/");
                                        referrer = separated[2];
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    long unixSeconds = Long.parseLong(time);
                                    Date date = new Date(unixSeconds * 1000L);
                                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
                                    String formattedDate = sdfDate.format(date);
                                    String formattedTime = sdfTime.format(date);

                                   /* if (!UserID.equals("1")) {
                                        if (user_id.equals(UserID)) {*/
                                            PojoChat user = new PojoChat();
                                            user.setId(id);
                                            user.setNick(nick);
                                            user.setUser_id(user_id);
                                            user.setIp(ip);
                                            user.setDep_id(dep_id);
                                            user.setEmail(email);
                                            user.setCountry_name(country_name);
                                            user.setPhone(phone);
                                            user.setLast_msg_id(last_msg_id);
                                            user.setCity(city);
                                            user.setBrowser(browser);
                                            user.setReferrer(referrer);
                                            user.setHash(hash);
                                            user.setTime(formattedTime);
                                            user.setDate(formattedDate);

                                            mUsersT.add(user);

                                            mUserAdapter = new UserAdapter();
                                            mRecyclerView.setAdapter(mUserAdapter);

                                            ivNoRecordTransfer.setVisibility(View.GONE);
                                            mRecyclerView.setVisibility(View.VISIBLE);
                                       /* }
                                    } else {
                                        PojoChat user = new PojoChat();
                                        user.setId(id);
                                        user.setNick(nick);
                                        user.setUser_id(user_id);
                                        user.setIp(ip);
                                        user.setDep_id(dep_id);
                                        user.setEmail(email);
                                        user.setCountry_name(country_name);
                                        user.setPhone(phone);
                                        user.setLast_msg_id(last_msg_id);
                                        user.setCity(city);
                                        user.setBrowser(browser);
                                        user.setReferrer(referrer);
                                        user.setHash(hash);
                                        user.setTime(formattedTime);
                                        user.setDate(formattedDate);

                                        mUsersT.add(user);

                                        mUserAdapter = new UserAdapter();
                                        mRecyclerView.setAdapter(mUserAdapter);

                                        ivNoRecordTransfer.setVisibility(View.GONE);
                                        mRecyclerView.setVisibility(View.VISIBLE);
                                    }*/
                                }
                            }

                            if (mUsersT.size() == 0) {

                                ivNoRecordTransfer.setVisibility(View.VISIBLE);
                                mRecyclerView.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

//                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {

                String auth = new String(user + ":" + password);
//                String auth = new String("admin:mipl@1234");
                byte[] data = auth.getBytes();
                String base64 = Base64.encodeToString(data, Base64.NO_WRAP);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Basic " + base64);
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView tvListName, tvListID, tvListLocation, tvListContact, tvListEmail, tvListDate, tvListTime, tvListDepartment;
        public LinearLayout llChatListCard;
        public ImageView ivListMenu;

        public UserViewHolder(View itemView) {
            super(itemView);
            tvListName = (TextView) itemView.findViewById(R.id.tvListName);
            tvListID = (TextView) itemView.findViewById(R.id.tvListID);
            tvListLocation = (TextView) itemView.findViewById(R.id.tvListLocation);
            tvListContact = (TextView) itemView.findViewById(R.id.tvListContact);
            tvListEmail = (TextView) itemView.findViewById(R.id.tvListEmail);
            tvListDate = (TextView) itemView.findViewById(R.id.tvListDate);
            tvListTime = (TextView) itemView.findViewById(R.id.tvListTime);
            tvListDepartment = (TextView) itemView.findViewById(R.id.tvListDepartment);
            llChatListCard = (LinearLayout) itemView.findViewById(R.id.llChatListCard);
            ivListMenu = (ImageView) itemView.findViewById(R.id.ivListMenu);
        }
    }

    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        }

        @Override
        public int getItemViewType(int position) {
            return mUsersT.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.chatlistcard, parent, false);
                return new UserViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof UserViewHolder) {
                final PojoChat user = mUsersT.get(position);
                final UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.tvListName.setText(user.getNick());
                userViewHolder.tvListID.setText("ID:" + user.getId());
                userViewHolder.tvListContact.setText("Contact:" + user.getPhone());
                userViewHolder.tvListEmail.setText("E-mail:" + user.getEmail());
                userViewHolder.tvListLocation.setText("Location:" + user.getCity());
                userViewHolder.tvListTime.setText("Time:" + user.getTime());
                userViewHolder.tvListDate.setText("Date:" + user.getDate());
                userViewHolder.tvListDepartment.setText("Department: " + user.getReferrer());

                userViewHolder.ivListMenu.setVisibility(View.GONE);

                userViewHolder.llChatListCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), ChatWindow.class);
                        intent.putExtra("chatID", user.getId());
                        intent.putExtra("Email", user.getEmail());
                        intent.putExtra("IP", user.getIp());
                        intent.putExtra("Country", user.getCountry_name());
                        intent.putExtra("From", user.getReferrer());
                        intent.putExtra("browser", user.getBrowser());
                        intent.putExtra("nick", user.getNick());
                        intent.putExtra("hash", user.getHash());
                        intent.putExtra("CHAT", "CLOSED");
                        startActivity(intent);
//                        getActivity().finish();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mUsersT == null ? 0 : mUsersT.size();
        }
    }
}