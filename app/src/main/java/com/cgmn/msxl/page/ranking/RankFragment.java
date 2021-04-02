package com.cgmn.msxl.page.ranking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.cgmn.msxl.R;
import com.cgmn.msxl.application.GlobalTreadPools;
import com.cgmn.msxl.comp.CustmerToast;
import com.cgmn.msxl.comp.adpter.UserRankAdpter;
import com.cgmn.msxl.data.RankEntity;
import com.cgmn.msxl.data.StockHolder;
import com.cgmn.msxl.handdler.GlobalExceptionHandler;
import com.cgmn.msxl.server_interface.BaseData;
import com.cgmn.msxl.service.GlobalDataHelper;
import com.cgmn.msxl.service.OkHttpClientManager;
import com.cgmn.msxl.utils.CommonUtil;
import com.cgmn.msxl.utils.ConstantHelper;
import com.cgmn.msxl.utils.MessageUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class RankFragment extends Fragment {
    private ListView listView;
    private Context mContext;
    private static final String trainTypeKey = "trainType";
    private static final String rankTypeKey = "rankType";
    private String trainType;
    private String rankType;
    private Handler mHandler;
    private List<RankEntity> mData = null;
    private UserRankAdpter adpter;
    private TextView txt_rank;

    public RankFragment() { }
    public static RankFragment newInstance(String trainType, String rankType) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
        args.putString(trainTypeKey, trainType);
        args.putString(rankTypeKey, rankType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            trainType = bundle.getString(trainTypeKey);
            rankType = bundle.getString(rankTypeKey);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View  view = inflater.inflate(R.layout.ranking_fragment, container, false);
        bindView(view);
        initAdpter();
        return view;
    }

    private void bindView(View view){
        mContext = view.getContext();
        listView = view.findViewById(R.id.list_content);
        txt_rank = view.findViewById(R.id.txt_rank);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == MessageUtil.REQUEST_SUCCESS) {
                    mData = (List<RankEntity>) msg.obj;
                    if(!CommonUtil.isEmpty(mData)){
                        adpter = new UserRankAdpter(mContext, mData, trainType);
                        listView.setAdapter(adpter);
                        String txt = "我的排名： ";
                        Integer rankNo = mData.get(0).getMyRank();
                        if(rankNo != null && rankNo>0){
                            txt_rank.setText(String.format("%s %s/%s", txt, rankNo, mData.get(0).getTotalAcc()));
                        }else{
                            txt_rank.setText(String.format("%s %s/%s", txt, "--", mData.get(0).getTotalAcc()));
                        }
                    }else{
//                        CustmerToast.makeText(mContext, getString(R.string.zan_no_rank)).show();
                    }
                } else if (msg.what == MessageUtil.EXCUTE_EXCEPTION) {
                    GlobalExceptionHandler.getInstance(mContext).handlerException((Exception) msg.obj);
                }
                return false;
            }
        });
        TextView txtEarn = view.findViewById(R.id.txtEarn);
        if(!trainType.equals(StockHolder.RANK_SUMMARY+"")){
            txtEarn.setText("收益");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private String getUrl(){
        String action = "/statistic/daily_ranking";
        if(rankType.equals("SUM")){
            action = "/statistic/total_ranking";
        }
        Map<String, String> params = new HashMap<>();
        params.put("train_type", trainType);
        params.put("token", GlobalDataHelper.getToken(mContext));
        return CommonUtil.buildGetUrl(
                ConstantHelper.serverUrl,
                action, params);
    }

    private void initAdpter() {
        //加载用户信息
        GlobalTreadPools.getInstance(mContext).execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClientManager.getAsyn(getUrl(),
                        new OkHttpClientManager.ResultCallback<BaseData>() {
                            @Override
                            public void onError(com.squareup.okhttp.Request request, Exception e) {
                                Message message = Message.obtain();
                                message.what = MessageUtil.EXCUTE_EXCEPTION;
                                message.obj = e;
                                mHandler.sendMessage(message);
                            }

                            @Override
                            public void onResponse(BaseData data) {
                                Message message = Message.obtain();
                                message.what = MessageUtil.REQUEST_SUCCESS;
                                try {
                                    message.obj = data.getRankList();
                                    Integer status = data.getStatus();
                                    if (status == null || status == -1) {
                                        throw new Exception(data.getError());
                                    }
                                } catch (Exception e) {
                                    message.what = MessageUtil.EXCUTE_EXCEPTION;
                                    message.obj = e;
                                }
                                mHandler.sendMessage(message);
                            }
                        });
            }
        });
    }

}