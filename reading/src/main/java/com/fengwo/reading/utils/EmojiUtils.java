package com.fengwo.reading.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.fengwo.reading.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmojiUtils {
    public static final String ee_1 = "[(a)]";
    public static final String ee_2 = "[(b)]";
    public static final String ee_3 = "[(c)]";
    public static final String ee_4 = "[(d)]";
    public static final String ee_5 = "[(e)]";
    public static final String ee_6 = "[(f)]";
    public static final String ee_7 = "[(g)]";
    public static final String ee_8 = "[(h)]";
    public static final String ee_9 = "[(i)]";
    public static final String ee_10 = "[(j)]";
    public static final String ee_11 = "[(k)]";
    public static final String ee_12 = "[(l)]";
    public static final String ee_13 = "[(m)]";
    public static final String ee_14 = "[(n)]";
    public static final String ee_15 = "[(o)]";
    public static final String ee_16 = "[(p)]";
    public static final String ee_17 = "[(q)]";
    public static final String ee_18 = "[(r)]";
    public static final String ee_19 = "[(s)]";
    public static final String ee_20 = "[(t)]";
    public static final String ee_21 = "[(u)]";
    public static final String ee_22 = "[(v)]";
    public static final String ee_23 = "[(w)]";
    public static final String ee_24 = "[(x)]";
    public static final String ee_25 = "[(w)]";
    public static final String ee_26 = "[(z)]";
    public static final String ee_27 = "[(A)]";
    public static final String ee_28 = "[(B)]";
    public static final String ee_29 = "[(C)]";
    public static final String ee_30 = "[(D)]";
    public static final String ee_31 = "[(E)]";
    public static final String ee_32 = "[(F)]";
    public static final String ee_33 = "[(G)]";
    public static final String ee_34 = "[(H)]";
    public static final String ee_35 = "[(I)]";
    public static final String ee_36 = "[(J)]";
    public static final String ee_37 = "[(K)]";
    public static final String ee_38 = "[(L)]";
    public static final String ee_39 = "[(M)]";
    public static final String ee_40 = "[(N)]";
    public static final String ee_41 = "[(O)]";
    public static final String ee_42 = "[(P)]";
    public static final String ee_43 = "[(Q)]";
    public static final String ee_44 = "[(R)]";
    public static final String ee_45 = "[(S)]";
    public static final String ee_46 = "[(T)]";
    public static final String ee_47 = "[(U)]";
    public static final String ee_48 = "[(V)]";
    public static final String ee_49 = "[(W)]";
    public static final String ee_50 = "[(X)]";
    public static final String ee_51 = "[(Y)]";
    public static final String ee_52 = "[(Z)]";
    public static final String ee_53 = "[(!)]";
    public static final String ee_54 = "[(@)]";
    public static final String ee_55 = "[(#)]";
    public static final String ee_56 = "[($)]";
    public static final String ee_57 = "[(%)]";
    public static final String ee_58 = "[(^)]";
    public static final String ee_59 = "[(&)]";
    public static final String ee_60 = "[(*)]";
    public static final String ee_61 = "[(()]";
    public static final String ee_62 = "[())]";
    public static final String ee_63 = "[(-)]";
    public static final String ee_64 = "[(_)]";
    public static final String ee_65 = "[(+)]";
    public static final String ee_66 = "[(=)]";
    public static final String ee_67 = "[({)]";
    public static final String ee_68 = "[(})]";
    public static final String ee_69 = "[(|)]";
    public static final String ee_70 = "[(;)]";
    public static final String ee_71 = "[(:)]";
    public static final String ee_72 = "[(,)]";
    public static final String ee_73 = "[(.)]";
    public static final String ee_74 = "[(?)]";
    public static final String ee_75 = "[(~)]";
    public static final String ee_76 = "[(`)]";
    public static final String ee_77 = "[(')]";
    public static final String ee_78 = "[(·)]";
    public static final String ee_79 = "[(…)]";
    public static final String ee_80 = "[(0)]";
    public static final String ee_81 = "[(1)]";
    public static final String ee_82 = "[(2)]";
    public static final String ee_83 = "[(3)]";
    public static final String ee_84 = "[(4)]";
    public static final String ee_85 = "[(5)]";
    public static final String ee_86 = "[(6)]";
    public static final String ee_87 = "[(7)]";
    public static final String ee_88 = "[(8)]";
    public static final String ee_89 = "[(9)]";
    public static final String ee_90 = "[:)a]";
    public static final String ee_91 = "[:)b]";
    public static final String ee_92 = "[:)c]";
    public static final String ee_93 = "[:)d]";
    public static final String ee_94 = "[:)e]";
    public static final String ee_95 = "[:)f]";
    public static final String ee_96 = "[:)g]";
    public static final String ee_97 = "[:)h]";
    public static final String ee_98 = "[:)i]";
    public static final String ee_99 = "[:)j]";
    public static final String ee_100 = "[:)k]";
    public static final String ee_101 = "[:)l]";
    public static final String ee_102 = "[:)m]";
    public static final String ee_103 = "[:)n]";
    public static final String ee_104 = "[:)o]";
    public static final String ee_105 = "[:)p]";
    public static final String ee_106 = "[:)q]";
    public static final String ee_107 = "[:)r]";
    public static final String ee_108 = "[:)s]";
    public static final String ee_109 = "[:)t]";
    public static final String ee_110 = "[:)u]";
    public static final String ee_111 = "[:)v]";
    public static final String ee_112 = "[:)w]";
    public static final String ee_113 = "[:)x]";
    public static final String ee_114 = "[:)y]";
    public static final String ee_115 = "[:)z]";
    public static final String ee_116 = "[:(a]";
    public static final String ee_117 = "[:(b]";
    public static final String ee_118 = "[:(c]";
    public static final String ee_119 = "[:(d]";
    public static final String ee_120 = "[:(e]";
    public static final String ee_121 = "[:(f]";
    public static final String ee_122 = "[:(g]";
    public static final String ee_123 = "[:(h]";
    public static final String ee_124 = "[:(i]";
    public static final String ee_125 = "[:(j]";
    public static final String ee_126 = "[:(k]";
    public static final String ee_127 = "[:(l]";
    public static final String ee_128 = "[:(m]";
    public static final String ee_129 = "[:(n]";
    public static final String ee_130 = "[:(o]";
    public static final String ee_131 = "[:(p]";
    public static final String ee_132 = "[:(q]";
    public static final String ee_133 = "[:(r]";
    public static final String ee_134 = "[:(s]";
    public static final String ee_135 = "[:(t]";
    public static final String ee_136 = "[:(u]";
    public static final String ee_137 = "[:(v]";
    public static final String ee_138 = "[:(w]";
    public static final String ee_139 = "[:(x]";
    public static final String ee_140 = "[:(y]";
    public static final String ee_141 = "[:(z]";

    private static final Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

    static {
        addPattern(emoticons, ee_1, R.drawable.ee_1);
        addPattern(emoticons, ee_2, R.drawable.ee_2);
        addPattern(emoticons, ee_3, R.drawable.ee_3);
        addPattern(emoticons, ee_4, R.drawable.ee_4);
        addPattern(emoticons, ee_5, R.drawable.ee_5);
        addPattern(emoticons, ee_6, R.drawable.ee_6);
        addPattern(emoticons, ee_7, R.drawable.ee_7);
        addPattern(emoticons, ee_8, R.drawable.ee_8);
        addPattern(emoticons, ee_9, R.drawable.ee_9);
        addPattern(emoticons, ee_10, R.drawable.ee_10);
        addPattern(emoticons, ee_11, R.drawable.ee_11);
        addPattern(emoticons, ee_12, R.drawable.ee_12);
        addPattern(emoticons, ee_13, R.drawable.ee_13);
        addPattern(emoticons, ee_14, R.drawable.ee_14);
        addPattern(emoticons, ee_15, R.drawable.ee_15);
        addPattern(emoticons, ee_16, R.drawable.ee_16);
        addPattern(emoticons, ee_17, R.drawable.ee_17);
        addPattern(emoticons, ee_18, R.drawable.ee_18);
        addPattern(emoticons, ee_19, R.drawable.ee_19);
        addPattern(emoticons, ee_20, R.drawable.ee_20);
        addPattern(emoticons, ee_21, R.drawable.ee_21);
        addPattern(emoticons, ee_22, R.drawable.ee_22);
        addPattern(emoticons, ee_23, R.drawable.ee_23);
        addPattern(emoticons, ee_24, R.drawable.ee_24);
        addPattern(emoticons, ee_25, R.drawable.ee_25);
        addPattern(emoticons, ee_26, R.drawable.ee_26);
        addPattern(emoticons, ee_27, R.drawable.ee_27);
        addPattern(emoticons, ee_28, R.drawable.ee_28);
        addPattern(emoticons, ee_29, R.drawable.ee_29);
        addPattern(emoticons, ee_30, R.drawable.ee_30);
        addPattern(emoticons, ee_31, R.drawable.ee_31);
        addPattern(emoticons, ee_32, R.drawable.ee_32);
        addPattern(emoticons, ee_33, R.drawable.ee_33);
        addPattern(emoticons, ee_34, R.drawable.ee_34);
        addPattern(emoticons, ee_35, R.drawable.ee_35);
        addPattern(emoticons, ee_36, R.drawable.ee_36);
        addPattern(emoticons, ee_37, R.drawable.ee_37);
        addPattern(emoticons, ee_38, R.drawable.ee_38);
        addPattern(emoticons, ee_39, R.drawable.ee_39);
        addPattern(emoticons, ee_40, R.drawable.ee_40);
        addPattern(emoticons, ee_41, R.drawable.ee_41);
        addPattern(emoticons, ee_42, R.drawable.ee_42);
        addPattern(emoticons, ee_43, R.drawable.ee_43);
        addPattern(emoticons, ee_44, R.drawable.ee_44);
        addPattern(emoticons, ee_45, R.drawable.ee_45);
        addPattern(emoticons, ee_46, R.drawable.ee_46);
        addPattern(emoticons, ee_47, R.drawable.ee_47);
        addPattern(emoticons, ee_48, R.drawable.ee_48);
        addPattern(emoticons, ee_49, R.drawable.ee_49);
        addPattern(emoticons, ee_50, R.drawable.ee_50);
        addPattern(emoticons, ee_51, R.drawable.ee_51);
        addPattern(emoticons, ee_52, R.drawable.ee_52);
        addPattern(emoticons, ee_53, R.drawable.ee_53);
        addPattern(emoticons, ee_54, R.drawable.ee_54);
        addPattern(emoticons, ee_55, R.drawable.ee_55);
        addPattern(emoticons, ee_56, R.drawable.ee_56);
        addPattern(emoticons, ee_57, R.drawable.ee_57);
        addPattern(emoticons, ee_58, R.drawable.ee_58);
        addPattern(emoticons, ee_59, R.drawable.ee_59);
        addPattern(emoticons, ee_60, R.drawable.ee_60);
        addPattern(emoticons, ee_61, R.drawable.ee_61);
        addPattern(emoticons, ee_62, R.drawable.ee_62);
        addPattern(emoticons, ee_63, R.drawable.ee_63);
        addPattern(emoticons, ee_64, R.drawable.ee_64);
        addPattern(emoticons, ee_65, R.drawable.ee_65);
        addPattern(emoticons, ee_66, R.drawable.ee_66);
        addPattern(emoticons, ee_67, R.drawable.ee_67);
        addPattern(emoticons, ee_68, R.drawable.ee_68);
        addPattern(emoticons, ee_69, R.drawable.ee_69);
        addPattern(emoticons, ee_70, R.drawable.ee_70);
        addPattern(emoticons, ee_71, R.drawable.ee_71);
        addPattern(emoticons, ee_72, R.drawable.ee_72);
        addPattern(emoticons, ee_73, R.drawable.ee_73);
        addPattern(emoticons, ee_74, R.drawable.ee_74);
        addPattern(emoticons, ee_75, R.drawable.ee_75);
        addPattern(emoticons, ee_76, R.drawable.ee_76);
        addPattern(emoticons, ee_77, R.drawable.ee_77);
        addPattern(emoticons, ee_78, R.drawable.ee_78);
        addPattern(emoticons, ee_79, R.drawable.ee_79);
        addPattern(emoticons, ee_80, R.drawable.ee_80);
        addPattern(emoticons, ee_81, R.drawable.ee_81);
        addPattern(emoticons, ee_82, R.drawable.ee_82);
        addPattern(emoticons, ee_83, R.drawable.ee_83);
        addPattern(emoticons, ee_84, R.drawable.ee_84);
        addPattern(emoticons, ee_85, R.drawable.ee_85);
        addPattern(emoticons, ee_86, R.drawable.ee_86);
        addPattern(emoticons, ee_87, R.drawable.ee_87);
        addPattern(emoticons, ee_88, R.drawable.ee_88);
        addPattern(emoticons, ee_89, R.drawable.ee_89);
        addPattern(emoticons, ee_90, R.drawable.ee_90);
        addPattern(emoticons, ee_91, R.drawable.ee_91);
        addPattern(emoticons, ee_92, R.drawable.ee_92);
        addPattern(emoticons, ee_93, R.drawable.ee_93);
        addPattern(emoticons, ee_94, R.drawable.ee_94);
        addPattern(emoticons, ee_95, R.drawable.ee_95);
        addPattern(emoticons, ee_96, R.drawable.ee_96);
        addPattern(emoticons, ee_97, R.drawable.ee_97);
        addPattern(emoticons, ee_98, R.drawable.ee_98);
        addPattern(emoticons, ee_99, R.drawable.ee_99);
        addPattern(emoticons, ee_100, R.drawable.ee_100);
        addPattern(emoticons, ee_101, R.drawable.ee_101);
        addPattern(emoticons, ee_102, R.drawable.ee_102);
        addPattern(emoticons, ee_103, R.drawable.ee_103);
        addPattern(emoticons, ee_104, R.drawable.ee_104);
        addPattern(emoticons, ee_105, R.drawable.ee_105);
        addPattern(emoticons, ee_106, R.drawable.ee_106);
        addPattern(emoticons, ee_107, R.drawable.ee_107);
        addPattern(emoticons, ee_108, R.drawable.ee_108);
        addPattern(emoticons, ee_109, R.drawable.ee_109);
        addPattern(emoticons, ee_110, R.drawable.ee_110);
        addPattern(emoticons, ee_111, R.drawable.ee_111);
        addPattern(emoticons, ee_112, R.drawable.ee_112);
        addPattern(emoticons, ee_113, R.drawable.ee_113);
        addPattern(emoticons, ee_114, R.drawable.ee_114);
        addPattern(emoticons, ee_115, R.drawable.ee_115);
        addPattern(emoticons, ee_116, R.drawable.ee_116);
        addPattern(emoticons, ee_117, R.drawable.ee_117);
        addPattern(emoticons, ee_118, R.drawable.ee_118);
        addPattern(emoticons, ee_119, R.drawable.ee_119);
        addPattern(emoticons, ee_120, R.drawable.ee_120);
        addPattern(emoticons, ee_121, R.drawable.ee_121);
        addPattern(emoticons, ee_122, R.drawable.ee_122);
        addPattern(emoticons, ee_123, R.drawable.ee_123);
        addPattern(emoticons, ee_124, R.drawable.ee_124);
        addPattern(emoticons, ee_125, R.drawable.ee_125);
        addPattern(emoticons, ee_126, R.drawable.ee_126);
        addPattern(emoticons, ee_127, R.drawable.ee_127);
        addPattern(emoticons, ee_128, R.drawable.ee_128);
        addPattern(emoticons, ee_129, R.drawable.ee_129);
        addPattern(emoticons, ee_130, R.drawable.ee_130);
        addPattern(emoticons, ee_131, R.drawable.ee_131);
        addPattern(emoticons, ee_132, R.drawable.ee_132);
        addPattern(emoticons, ee_133, R.drawable.ee_133);
        addPattern(emoticons, ee_134, R.drawable.ee_134);
        addPattern(emoticons, ee_135, R.drawable.ee_135);
        addPattern(emoticons, ee_136, R.drawable.ee_136);
        addPattern(emoticons, ee_137, R.drawable.ee_137);
        addPattern(emoticons, ee_138, R.drawable.ee_138);
        addPattern(emoticons, ee_139, R.drawable.ee_139);
        addPattern(emoticons, ee_140, R.drawable.ee_140);
        addPattern(emoticons, ee_141, R.drawable.ee_141);
    }

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                                   int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    /**
     * replace existing spannable with smiles
     *
     * @param context
     * @param spannable
     * @return
     */
    public static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class)) {
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end()) {
                        spannable.removeSpan(span);
                    } else {
                        set = false;
                        break;
                    }
                }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    public static boolean containsKey(String key) {
        boolean b = false;
        for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(key);
            if (matcher.find()) {
                b = true;
                break;
            }
        }
        return b;
    }

}