package com.fengwo.reading.main.my;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fengwo.reading.R;
import com.fengwo.reading.activity.BaseActivity;

/**
 * 有书服务协议
 * author song
 */
public class WithYouShuAgreement extends BaseActivity {
    TextView tv_agreement_content ;
    private ImageView iv_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_you_shu_agreement);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        tv_agreement_content = (TextView) findViewById(R.id.tv_agreement_content);
        tv_agreement_content.append("有书在此特别提醒用户认真阅读、充分理解本《有书服务使用协议》（下称《协议》）。请您审慎阅读后，选择接受或不接受本《协议》（未成年人应在法定监护人陪同下阅读）。除非您接受本《协议》所有条款，否则您无权下载、安装或使用本软件及其相关服务。您的下载、安装、使用、帐号获取和登录等行为将被视为对本《协议》的接受，并表示您同意受本《协议》各项条款的约束。"+"\n"+"\n");
        tv_agreement_content.append("知识产权声明"+"\n"+"\n");
        tv_agreement_content.append("1、有书是一个信息获取、分享及传播的平台，我们尊重和鼓励有书用户创作内容，同时，有书的一切著作权、商标权、专利权等知识产权和商业秘密，以及与有书相关的所有信息内容等，除涉及第三方授权的软件或技术外，有书均享有知识产权。"+"\n"+"\n");
        tv_agreement_content.append("2、未经有书书面同意，用户不得以任何营利性[一般非营利性的目的是可以的]目的自行实施、利用、转让或许可第三方实施、利用、转让上述知识产权，有书将保留追究相关法律责任的权利。"+"\n"+"\n");
        tv_agreement_content.append("3、第三方若出于非商业目的，将用户在有书上发表的内容进行转载使用的，应当在作品正文开头的显著位置注明原作者姓名（或原作者在有书上使用的帐号名称），并给出原始链接。若需要对作品进行修改，或用于商业目的，第三方应当联系用户获得单独授权后方可实施。"+"\n"+"\n");
        tv_agreement_content.append("4、用户在有书上传或发表的内容，应保证该内容不会侵犯任何第三方的合法权益。如果第三方提出关于著作权的异议，有书有权根据实际情况删除相关的内容，并保留追究相关用户的法律责任，给有书或任何第三方造成损失的，用户须按实际损失或国家相关标准给予赔偿。"+"\n"+"\n"+"个人隐私"+"\n"+"\n"+"有书将通过技术手段、强化内部管理等办法充分保护用户的个人隐私信息，除用户明确授权或法律法规相关规定的原因外，有书保证不对外公开或向第三方透露用户个人隐私信息及用户在使用服务时存储的非公开内容。"+"\n"+"\n"+"\n"+"\n"+"使用须知"+"\n"+"\n"+"1、您了解并且同意使用本软件及服务，将遵守本条款中的权利义务，并对所有在您的注册名下发生的一切行为负责；"+"\n"+"\n"+"2、您了解并且同意有书不能为用户行为负责。使用本软件及配套服务产生的所有风险由个人承担，有书不对本软件及配套服务的合法性、适用性、安全性、做[无毒性包含于安全性中]任何形式的保证。如果您发现本软件及服务中存在任何违法违规行为，有义务及时向有书举报。"+"\n"+"\n"+"3、用户须遵守以下严禁事宜：利用本软件及服务传播反对宪法所确定的基本原则的内容；利用本软件及服务传播危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的内容；利用本软件及服务传播损害国家荣誉和利益的内容；利用本软件及服务传播煽动民族仇恨、民族歧视，破坏民族团结的内容；利用本软件及服务传播破坏国家宗教政策，宣扬邪教和封建迷信的内容；利用本软件及服务散布谣言，扰乱社会秩序，破坏社会稳定的内容；利用本软件及服务散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的内容；利用本软件及服务传播侮辱或者诽谤他人，侵害他人合法权益的内容；利用本软件及服务传播法律、行政法规禁止的其他内容等。"+"\n"+"\n"+"4、如因用户不遵守本条上述任意一款或者几款的规定，有书有权在不事先通知用户的情况下将相应的内容删除，或采取终止、完全或部分中止、限制用户帐号的使用功能或停止用户使用本软件或服务。"+"\n"+"\n"+"免责申明"+"\n"+"\n"+"1、用户在有书发表的内容仅表明其个人的立场和观点，并不代表有书的立场或观点。作为内容的发表者，需自行对所发表内容负责，因所发表内容引发的一切纠纷，由该内容的发表者承担全部法律及连带责任。有书不承担任何法律及连带责任。"+"\n"+"\n"+"2、对于因不可抗力造成的网络服务中断或其它缺陷，有书不承担任何责任，但将尽力减少因此而给用户造成的损失和影响。"+"\n"+"\n"+"服务及协议条款的变更"+"\n"+"\n"+"有书有权在必要时修改本协议条款，协议条款一旦发生变动，将会在相关页面上公布修改后的协议条款。如果不同意所改动的内容，用户应主动及时取消此项服务。如果用户继续使用服务，则视为接受协议条款的变动。\n" +
                "有书保留一切解释权利。"+"\n"+"\n");
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.in_from_left,
                        R.anim.out_to_right);
            }
        });
    }



}
