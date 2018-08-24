package com.colerobinette.cryptowatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

class BalanceAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    public BalanceAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Balance.trackedIds.size();
    }

    @Override
    public Object getItem(int index) {
        return Balance.trackedIds.get(index);
    }

    @Override
    public long getItemId(int index) {
        return index;
    }

    @Override
    public View getView(final int index, View convertView, ViewGroup parent) {
        // get the id for this index
        String id = (String) getItem(index);

        // get the view associated with this listview item
        View view = inflater.inflate(R.layout.list_item_balance, parent, false);

        // set the symbol text
        ((TextView) view.findViewById(R.id.coinName)).setText(id);

        // set the holdings text
        ((TextView) view.findViewById(R.id.coinBalance)).setText(String.format("%1$.3f", Balance.GetCoinHoldings(id)));

        // set the icon of the listview item
        File file = new File(Balance.actContext.getApplicationInfo().dataDir + "/" + id + ".png");
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            ((ImageView) view.findViewById(R.id.coinIcon)).setImageURI(uri);
        } else {
            ((ImageView) view.findViewById(R.id.coinIcon)).setImageResource(R.drawable.error);
        }

        // set the price text
        ((TextView) view.findViewById(R.id.coinPrice)).setText(Balance.GetCurrencySign() + String.format("%1$,.2f", Balance.GetCoinPrice(id)));

        // set the value of the holdings text
        ((TextView) view.findViewById(R.id.coinValue)).setText(Balance.GetCurrencySign() + String.format("%1$,.2f", Balance.GetCoinValue(id)));

        // set the percent changed text
        double change = Balance.GetCoinPriceChange(id);
        TextView changeText = (TextView) view.findViewById(R.id.percentChange);
        if (change > 0) {
            changeText.setText("+" + String.format("%1$,.2f", change) + "%");
            changeText.setTextColor(Color.rgb(0, 150, 0));
            ((ImageView) view.findViewById(R.id.changeArrow)).setImageResource(R.drawable.arrow_green);
        } else if (change < 0) {
            changeText.setText(String.format("%1$,.2f", change) + "%");
            changeText.setTextColor(Color.RED);
            ((ImageView) view.findViewById(R.id.changeArrow)).setImageResource(R.drawable.arrow_red);
        } else {
            changeText.setText("+" + String.format("%1$,.2f", change) + "%");
            changeText.setTextColor(Color.BLACK);
            ((ImageView) view.findViewById(R.id.changeArrow)).setImageResource(R.drawable.dash);
        }

        return view;
    }
}

class SymbolMap {
    static String mapString = "{\"42\":\"42-Coin\",\"300\":\"300-token\",\"365\":\"365Coin\",\"404\":\"404Coin\",\"611\":\"SixEleven\",\"808\":\"808\",\"888\":\"Octocoin\",\"1337\":\"1337\",\"2015\":\"2015-coin\",\"ARC*\":\"Arcade-City\",\"CLUB\":\"ClubCoin\",\"007\":\"007-coin\",\"ZCN\":\"0chain\",\"ZRX\":\"0x\",\"0xBTC\":\"0xBitcoin\",\"BIT16\":\"16BitCoin\",\"MCT\":\"1717-Masonic-Commemorative-Token\",\"1CR\":\"1Credit\",\"CHAO\":\"23-Skidoo\",\"2BACCO\":\"2BACCO-Coin\",\"2GIVE\":\"2GiveCoin\",\"32BIT\":\"32Bitcoin\",\"3DES\":\"3DES\",\"8BT\":\"8-Circuit-Studios\",\"8BIT\":\"8BIT-Coin\",\"ATKN\":\"A-Token\",\"RTB\":\"AB-CHAIN\",\"ABC\":\"AB-Chain\",\"AC3\":\"AC3\",\"ACT\":\"ACT\",\"ACOIN\":\"ACoin\",\"AEON\":\"AEON\",\"AIC\":\"AI-Crypto\",\"AIDOC\":\"AI-Doctor\",\"AIT\":\"AIChain-Token\",\"XAI*\":\"AICoin\",\"AITT\":\"AITrading\",\"AXT\":\"AIX\",\"ALX\":\"ALAX\",\"ALIS\":\"ALISmedia\",\"ALT\":\"ALTcoin\",\"AMBT\":\"AMBT-Token\",\"AMIS\":\"AMIS\",\"ANTS\":\"ANTS-Reloaded\",\"APIS\":\"APIS\",\"ARE\":\"ARENON\",\"ARK\":\"ARK\",\"ARNA\":\"ARNA-Panacea\",\"ATB\":\"ATB-coin\",\"ATCC\":\"ATC-Coin\",\"ATFS\":\"ATFS-Project\",\"ATL\":\"ATLANT\",\"ATM\":\"ATMChain\",\"AUC*\":\"AU-Coin\",\"AXR\":\"AXRON\",\"AXS\":\"AXS\",\"ABJ\":\"Abjcoin\",\"ABS\":\"Absolute-Coin\",\"ACC*\":\"Accelerator-Network\",\"ACCO\":\"Accolade\",\"AEC\":\"AcesCoin\",\"ACES\":\"AcesCoin\",\"ACT*\":\"Achain\",\"ACH\":\"AchieveCoin\",\"ACID\":\"AcidCoin\",\"OAK\":\"Acorn-Collective\",\"ACTN\":\"Action-Coin\",\"AMT\":\"Acumen\",\"ACC\":\"AdCoin\",\"ADX\":\"AdEx\",\"ADT\":\"AdToken\",\"ADM\":\"Adamant\",\"ADB\":\"Adbank\",\"ADL\":\"Adelphoi\",\"ADH\":\"Adhive\",\"ADST\":\"Adshares\",\"ABT*\":\"Advanced-Browsing-Token\",\"AIB\":\"AdvancedInternetBlock\",\"ADZ\":\"Adzcoin\",\"AGS\":\"Aegis\",\"AERM\":\"Aerium\",\"AERO\":\"Aero-Coin\",\"AM\":\"AeroMe\",\"ARN\":\"Aeron\",\"AE\":\"Aeternity\",\"AET\":\"AfterEther\",\"AGRS\":\"Agoras-Token\",\"DLT\":\"Agrello-Delta\",\"AHT\":\"Ahoolee\",\"AID\":\"AidCoin\",\"ADN\":\"Aiden\",\"ADK\":\"Aidos-Kuneen\",\"AIX\":\"Aigang\",\"AION\":\"Aion\",\"AST\":\"AirSwap\",\"AIR\":\"AirToken\",\"AIR*\":\"Aircoin\",\"AKA\":\"Akroma\",\"ALEX\":\"Alexandrite\",\"PLM\":\"Algo.Land\",\"ALG\":\"Algory\",\"ALN\":\"AlienCoin\",\"SOC\":\"All-Sports-Coin\",\"ASAFE2\":\"Allsafe\",\"APC\":\"AlpaCoin\",\"ALPS\":\"Alpenschillling\",\"ALF\":\"AlphaCoin\",\"ACAT\":\"Alphacat\",\"ALQO\":\"Alqo\",\"ALTCOM\":\"AltCommunity-Coin\",\"ALTOCAR\":\"AltoCar\",\"AMBER\":\"AmberCoin\",\"AMB\":\"Ambrosus\",\"AMC\":\"AmericanCoin\",\"AMX\":\"Amero\",\"AMMO\":\"Ammo-Rewards\",\"AMO\":\"Amo-Coin\",\"AMN\":\"Amon\",\"AMS\":\"Amsterdam-Coin\",\"AMY\":\"Amygws\",\"ANCP\":\"Anacrypt\",\"ANAL\":\"AnalCoin\",\"ACP\":\"Anarchists-Prime\",\"AND\":\"AndromedaCoin\",\"ANGL\":\"Angel-Token\",\"AVH\":\"Animation-Vision-Cash\",\"ANI\":\"Animecoin\",\"ANK\":\"Ankorus-Token\",\"ANC\":\"Anoncoin\",\"RYZ\":\"Anryze\",\"ANTI\":\"Anti-Bitcoin\",\"ANTC\":\"AntiLitecoin\",\"CPX\":\"Apex-Token\",\"APEX\":\"ApexCoin\",\"APH\":\"Aphelion\",\"APPC\":\"AppCoins\",\"APT\":\"Aptcoin\",\"APX\":\"Apx\",\"ARCO\":\"AquariusCoin\",\"AR*\":\"Ar.cash\",\"ALC\":\"Arab-League-Coin\",\"ANT\":\"Aragon\",\"ARBI\":\"Arbi\",\"ARB\":\"Arbit-Coin\",\"ARCT\":\"ArbitrageCT\",\"ABT\":\"ArcBlock\",\"ARCH\":\"ArchCoin\",\"ARC\":\"ArcticCoin\",\"ARDR\":\"Ardor\",\"ARENA\":\"Arena\",\"ARG\":\"Argentum\",\"ARGUS\":\"ArgusCoin\",\"ARI\":\"AriCoin\",\"ARO\":\"Arionum\",\"BOTS\":\"ArkDAO\",\"ARM\":\"Armory-Coin\",\"ARPA\":\"ArpaCoin\",\"ABY\":\"ArtByte\",\"ARTE\":\"Artemine\",\"ATX\":\"ArtexCoin\",\"AUA\":\"ArubaCoin\",\"ASN\":\"Ascension-Coin\",\"XAS\":\"Asch\",\"AC\":\"Asia-Coin\",\"ADCN\":\"Asiadigicoin\",\"AST*\":\"Astral\",\"ASTRO\":\"Astronaut\",\"ATH\":\"Atheios\",\"ATMOS\":\"Atmos\",\"ATOM\":\"Atomic-Coin\",\"ATMI\":\"Atonomi\",\"AUC\":\"Auctus\",\"ADC\":\"AudioCoin\",\"REP\":\"Augur\",\"AURS\":\"Aureus\",\"AURA\":\"Aurora\",\"AOA\":\"Aurora-\",\"AUR\":\"Aurora-Coin\",\"AUN\":\"Authoreon\",\"ATS\":\"Authorship\",\"NIO*\":\"Autonio\",\"AUT\":\"Autoria\",\"ATM*\":\"Autumncoin\",\"AVL\":\"Avalanche\",\"AVA\":\"Avalon\",\"AV\":\"Avatar-Coin\",\"AVT\":\"AventCoin\",\"AOP\":\"Averopay\",\"AVE\":\"Avesta\",\"ACN\":\"AvonCoin\",\"AXIOM\":\"Axiom-Coin\",\"AXYS\":\"Axys\",\"AZART\":\"Azart\",\"B2B\":\"B2BX\",\"B3\":\"B3-Coin\",\"KB3\":\"B3Coin\",\"BAX\":\"BABB\",\"BAM\":\"BAM\",\"BANCA\":\"BANCA\",\"BKX\":\"BANKEX\",\"BBN\":\"BBNCOIN\",\"BERN\":\"BERNcash\",\"BEX\":\"BEX-token\",\"BFT\":\"BF-Token\",\"VEE\":\"BLOCKv\",\"BMT\":\"BMChain\",\"BOOM\":\"BOOM-Coin\",\"BOS\":\"BOScoin\",\"BQC\":\"BQCoin\",\"BRAT\":\"BROTHER\",\"BTCL\":\"BTC-Lite\",\"BTCM\":\"BTCMoon\",\"BAN\":\"Babes-and-Nerds\",\"BKC\":\"Balkancoin\",\"NANAS\":\"BananaBits\",\"BNT\":\"Bancor-Network-Token\",\"B@\":\"BankCoin\",\"BNK\":\"Bankera\",\"BCOIN\":\"BannerCoin\",\"BBN*\":\"Banyan-Network\",\"BBCC\":\"BaseballCardCoin\",\"BASHC\":\"BashCoin\",\"BAT\":\"Basic-Attention-Token\",\"BTA\":\"Bata\",\"BCX\":\"BattleCoin\",\"BSTK\":\"BattleStake\",\"SAND\":\"BeachCoin\",\"BRDD\":\"BeardDollars\",\"XBTS\":\"Beats\",\"BVC\":\"BeaverCoin\",\"BEE\":\"Bee-Token\",\"BFDT\":\"Befund\",\"BELA\":\"BelaCoin\",\"BBI\":\"BelugaPay\",\"BMK\":\"Benchmark\",\"BNC\":\"Benjacoin\",\"BEN*\":\"Benjamins\",\"BENJI\":\"BenjiRolls\",\"BEST\":\"BestChain\",\"KNG\":\"BetKings\",\"BET\":\"BetaCoin\",\"BTRM\":\"Betrium-Token\",\"BETR\":\"BetterBetting\",\"BETT\":\"Bettium\",\"BZNT\":\"Bezant\",\"BEZ\":\"Bezop\",\"BBP\":\"BiblePay\",\"BIX\":\"BiboxCoin\",\"BID\":\"BidCoin\",\"BDP\":\"Bidipass\",\"HUGE\":\"BigCoin\",\"LFC\":\"BigLifeCoin\",\"BIGUP\":\"BigUp\",\"BBO\":\"Bigbom\",\"BHC\":\"BighanCoin\",\"BIC\":\"Bikercoins\",\"BLRY\":\"BillaryCoin\",\"XBL\":\"Billionaire-Token\",\"BNB\":\"Binance-Coin\",\"BRC*\":\"BinaryCoin\",\"BIOB\":\"BioBar\",\"BIO\":\"Biocoin\",\"BIOS\":\"BiosCrypto\",\"BTRN\":\"Biotron\",\"BIP\":\"BipCoin\",\"BIS\":\"Bismuth\",\"BAS\":\"BitAsean\",\"BTB\":\"BitBar\",\"BAY\":\"BitBay\",\"BITB\":\"BitBean\",\"BBK\":\"BitBlocks\",\"BBT\":\"BitBoost\",\"BOSS\":\"BitBoss\",\"BRONZ\":\"BitBronze\",\"BCD*\":\"BitCAD\",\"BEN\":\"BitCOEN\",\"BITCAR\":\"BitCar\",\"CAT\":\"BitClave\",\"COAL\":\"BitCoal\",\"BCCOIN\":\"BitConnect-Coin\",\"BCR\":\"BitCredit\",\"BTCRY\":\"BitCrystal\",\"BCY\":\"BitCrystals\",\"BTCR\":\"BitCurrency\",\"BDG\":\"BitDegree\",\"CSNO\":\"BitDice\",\"BFX\":\"BitFinex-Tokens\",\"FLIP\":\"BitFlip\",\"FLX*\":\"BitFlux\",\"BTG*\":\"BitGem\",\"HIRE*\":\"BitHIRE\",\"STU\":\"BitJob\",\"BTLC\":\"BitLuckCoin\",\"LUX*\":\"BitLux\",\"BTM\":\"BitMark\",\"BMX\":\"BitMart-Coin\",\"BTMI\":\"BitMiles\",\"BM\":\"BitMoon\",\"BITOK\":\"BitOKX\",\"BTQ\":\"BitQuark\",\"RNTB\":\"BitRent\",\"BIT\":\"BitRewards\",\"BITX\":\"BitScreener\",\"XSEED\":\"BitSeeds\",\"BSD*\":\"BitSend\",\"BTE*\":\"BitSerial\",\"BSR\":\"BitSoar-Coin\",\"BSTN\":\"BitStation\",\"BST\":\"BitStone\",\"SWIFT\":\"BitSwift\",\"BXT\":\"BitTokens\",\"TUBE\":\"BitTube\",\"VEG\":\"BitVegan\",\"VOLT\":\"BitVolt\",\"ZNY\":\"BitZeny\",\"BTCA\":\"Bitair\",\"BAC\":\"BitalphaCoin\",\"BXC\":\"Bitcedi\",\"BTD\":\"Bitcloud\",\"BTDX\":\"Bitcloud-2.0\",\"BTCN\":\"BitcoiNote\",\"BTC\":\"Bitcoin\",\"BCA\":\"Bitcoin-Atom\",\"CDY\":\"Bitcoin-Candy\",\"BCC\":\"Bitcoin-Cash\",\"BTCC\":\"Bitcoin-Core\",\"BCD\":\"Bitcoin-Diamond\",\"BTG\":\"Bitcoin-Gold\",\"BITG\":\"Bitcoin-Green\",\"BTCH\":\"Bitcoin-Hush\",\"XBI\":\"Bitcoin-Incognito\",\"BCI\":\"Bitcoin-Interest\",\"BTN\":\"Bitcoin-Nova\",\"BTPL\":\"Bitcoin-Planet\",\"BTCP\":\"Bitcoin-Private\",\"BTCRED\":\"Bitcoin-Red\",\"RBTC\":\"Bitcoin-Revolution\",\"BCR*\":\"Bitcoin-Royal\",\"BTCS\":\"Bitcoin-Scrypt\",\"BT2\":\"Bitcoin-SegWit2X\",\"BTCS*\":\"Bitcoin-Supreme\",\"BTCD\":\"BitcoinDark\",\"BTCE*\":\"BitcoinEX\",\"BCF\":\"BitcoinFast\",\"BIFI\":\"BitcoinFile\",\"BTF*\":\"BitcoinFor\",\"BTCGO\":\"BitcoinGo\",\"XBC\":\"BitcoinPlus\",\"BTX*\":\"BitcoinTX\",\"BWS\":\"BitcoinWSpectrum\",\"BTW\":\"BitcoinWhite\",\"BCX*\":\"BitcoinX\",\"BTCZ\":\"BitcoinZ\",\"BM*\":\"Bitcomo\",\"BTX\":\"Bitcore\",\"DARX\":\"Bitdaric\",\"BDL\":\"Bitdeal\",\"BT1\":\"Bitfinex-Bitcoin-Future\",\"BTCL*\":\"BitluckCoin\",\"BIM\":\"BitminerCoin\",\"BMXT\":\"Bitmxittz\",\"XPAT\":\"Bitnation-Pangea\",\"BQ\":\"Bitqy\",\"BRO\":\"Bitradio\",\"BTL\":\"Bitrolium\",\"BITSD\":\"Bits-Digit\",\"BINS\":\"Bitsense\",\"BTS\":\"Bitshares\",\"BSX\":\"Bitspace\",\"XBS\":\"Bitstake\",\"BITS\":\"BitstarCoin\",\"BWT\":\"Bittwatt\",\"BITZ\":\"Bitz-Coin\",\"BTZ\":\"BitzCoin\",\"XBP\":\"Black-Pearl-Coin\",\"BLK\":\"BlackCoin\",\"BS\":\"BlackShadowCoin\",\"BHC*\":\"BlackholeCoin\",\"BMC\":\"Blackmoon-Crypto\",\"BSTAR\":\"Blackstar\",\"BLC\":\"BlakeCoin\",\"BLAS\":\"BlakeStar\",\"BLAZR\":\"BlazerCoin\",\"BLITZ\":\"BlitzCoin\",\"XBP*\":\"BlitzPredict\",\"ARY\":\"Block-Array\",\"CAT*\":\"BlockCAT\",\"BCDN\":\"BlockCDN-\",\"LNC\":\"BlockLancer\",\"BCPT\":\"BlockMason-Credit-Protocol\",\"BMH\":\"BlockMesh\",\"BLOCK\":\"BlockNet\",\"BLOCKPAY\":\"BlockPay\",\"BPL\":\"BlockPool\",\"BCAP\":\"Blockchain-Capital\",\"BLX\":\"Blockchain-Index\",\"BCT\":\"Blockchain-Terminal\",\"BTF\":\"Blockchain-Traded-Fund\",\"BCIO\":\"Blockchain.io\",\"BPT\":\"Blockport\",\"TIX\":\"Blocktix\",\"BTT\":\"Blocktrade-token\",\"BNTN\":\"Blocnation\",\"BLT\":\"Bloom-Token\",\"CDT\":\"Blox\",\"BLU\":\"BlueCoin\",\"BDR\":\"BlueDragon\",\"BLZ\":\"Bluzelle\",\"BNX\":\"BnrtxCoin\",\"BNB*\":\"Boats-and-Bitches\",\"BOB*\":\"Bob-Coin\",\"BOT\":\"Bodhi\",\"BOE\":\"Bodhi-\",\"BOG\":\"Bogcoin\",\"BOLD\":\"Bold\",\"BLN*\":\"Bolenum\",\"BOLI\":\"BolivarCoin\",\"BOMB\":\"BombCoin\",\"BON*\":\"BonesCoin\",\"BON\":\"Bonpay\",\"BBR\":\"Boolberry\",\"BOST\":\"BoostCoin\",\"BOSON\":\"BosonCoin\",\"BOTC\":\"BotChain\",\"CAP\":\"BottleCaps\",\"BTO\":\"Bottos\",\"BOU\":\"Boulle\",\"XBTY\":\"Bounty\",\"BNTY\":\"Bounty0x\",\"AHT*\":\"Bowhead-Health\",\"BSC\":\"BowsCoin\",\"BOXY\":\"BoxyCoin\",\"BRAIN\":\"BrainCoin\",\"BRD\":\"Bread-token\",\"BRX\":\"Breakout-Stake\",\"BRK\":\"BreakoutCoin\",\"BRIA\":\"Briacoin\",\"BBT*\":\"BrickBlock\",\"BCO\":\"BridgeCoin\",\"BRC\":\"BrightCoin\",\"BRIT\":\"BritCoin\",\"BUBO\":\"Budbo\",\"BGL\":\"Buglab\",\"BT\":\"BuildTeam\",\"BULLS\":\"BullshitCoin\",\"BWK\":\"Bulwark\",\"BUN\":\"BunnyCoin\",\"BURST\":\"BurstCoin\",\"BUZZ\":\"BuzzCoin\",\"BYC\":\"ByteCent\",\"BTE\":\"ByteCoin\",\"BCN\":\"ByteCoin\",\"GBYTE\":\"Byteball\",\"BTH\":\"Bytether \",\"BTM*\":\"Bytom\",\"XCT\":\"C-Bits\",\"CAIx\":\"CAIx\",\"CBD\":\"CBD-Crystals\",\"CCC\":\"CCCoin\",\"CEDEX\":\"CEDEX-Coin\",\"CEEK\":\"CEEK-Smart-VR-Token\",\"CETI\":\"CETUS-Coin\",\"CHIPS\":\"CHIPS\",\"CINNI\":\"CINNICOIN\",\"CLAM\":\"CLAMS\",\"CO2\":\"CO2-Token\",\"CMS\":\"COMSA\",\"CPY\":\"COPYTRACK\",\"COSS\":\"COSS\",\"CPC*\":\"CPChain\",\"MLS\":\"CPROP\",\"CMZ\":\"CRYPTOMAGZ\",\"CAB\":\"CabbageUnit\",\"CACH\":\"Cachecoin\",\"CF\":\"Californium\",\"CALC\":\"CaliphCoin\",\"CLO\":\"Callisto-Network\",\"CAM\":\"Camcoin\",\"CMPCO\":\"CampusCoin\",\"CAN\":\"CanYaCoin\",\"CND*\":\"Canada-eCoin\",\"CDN\":\"Canada-eCoin\",\"CCN\":\"CannaCoin\",\"XCI\":\"Cannabis-Industry-Coin\",\"CANN\":\"CannabisCoin\",\"XCD*\":\"Capdax\",\"CAPP\":\"Cappasity\",\"CPC\":\"CapriCoin\",\"CAR\":\"CarBlock-\",\"CTX\":\"CarTaxi\",\"CV\":\"CarVertical\",\"CARBON\":\"Carboncoin\",\"ADA\":\"Cardano\",\"CARD\":\"Cardstack\",\"CARE*\":\"Care-Token\",\"CARE\":\"Carebit\",\"CXO\":\"CargoX\",\"DIEM\":\"CarpeDiemCoin\",\"CTC\":\"CarterCoin\",\"CNBC\":\"Cash-&-Back-Coin\",\"CASH*\":\"Cash-Poker-Pro\",\"CBC\":\"CashBagCoin\",\"CBC*\":\"CashBet-Coin\",\"CASH\":\"CashCoin\",\"CSH\":\"CashOut\",\"CAS\":\"Cashaa\",\"CSC\":\"CasinoCoin\",\"CSTL\":\"Castle\",\"CAT1\":\"Catcoin\",\"CVTC\":\"CavatCoin\",\"CAV\":\"Caviar\",\"CCO\":\"Ccore\",\"CEL\":\"Celsius-Network\",\"CTR\":\"Centra\",\"CNT\":\"Centurion\",\"CBS\":\"Cerberus\",\"XCE\":\"Cerium\",\"CHC\":\"ChainCoin\",\"LINK\":\"ChainLink\",\"CHAN\":\"ChanCoin\",\"CAG\":\"Change\",\"CHA\":\"Charity-Coin\",\"CHARM\":\"Charm-Coin\",\"CHAT\":\"ChatCoin\",\"CXC\":\"CheckCoin\",\"CHESS\":\"ChessCoin\",\"CHILD\":\"ChildCoin\",\"CHI\":\"Chimaera\",\"CNC\":\"ChinaCoin\",\"CHIP\":\"Chip\",\"CHOOF\":\"ChoofCoin\",\"DAY\":\"Chronologic\",\"CRX\":\"ChronosCoin\",\"CIN\":\"CinderCoin\",\"CND\":\"Cindicator\",\"CIR\":\"CircuitCoin\",\"COVAL\":\"Circuits-of-Value\",\"CVC\":\"Civic\",\"XCLR\":\"ClearCoin\",\"POLL\":\"ClearPoll\",\"CLV\":\"CleverCoin\",\"CHASH\":\"CleverHash\",\"CLICK\":\"Clickcoin\",\"CLIN\":\"Clinicoin\",\"CLINT\":\"Clinton\",\"CLOAK\":\"CloakCoin\",\"CKC\":\"Clockcoin\",\"CLD\":\"Cloud\",\"CLOUT\":\"Clout\",\"CLUD\":\"CludCoin\",\"COE\":\"CoEval\",\"COB\":\"Cobinhood\",\"COX\":\"CobraCoin\",\"CTT\":\"CodeTract\",\"CFC\":\"CoffeeCoin\",\"CFI\":\"Cofound.it\",\"COG\":\"Cognitio\",\"COIN*\":\"Coin\",\"XMG\":\"Coin-Magi\",\"BTTF\":\"Coin-to-the-Future\",\"C2\":\"Coin.2\",\"CONI\":\"CoinBene\",\"CET\":\"CoinEx-token\",\"COFI\":\"CoinFi\",\"XCJ\":\"CoinJob\",\"CL\":\"CoinLancer\",\"LION\":\"CoinLion\",\"MEE\":\"CoinMeet\",\"MEET\":\"CoinMeet\",\"XCM\":\"CoinMetro\",\"CPL\":\"CoinPlace-Token\",\"CHP\":\"CoinPoker-Token\",\"LAB*\":\"CoinWorksCoin\",\"CTIC\":\"Coinmatic\",\"COI\":\"Coinnec\",\"CNO\":\"Coino\",\"CNMT\":\"Coinomat\",\"CXT\":\"Coinonat\",\"XCXT\":\"CoinonatX\",\"COLX\":\"ColossusCoinXT\",\"CLN\":\"Colu-Local-Network\",\"CMT\":\"CometCoin\",\"CBT\":\"CommerceBlock-Token\",\"CMM\":\"Commercium-\",\"CDX\":\"Commodity-Ad-Network\",\"COMM\":\"Community-Coin\",\"COC\":\"Community-Coin\",\"CMP\":\"Compcoin\",\"COMP\":\"Compound-Coin\",\"CPN\":\"CompuCoin\",\"CYC\":\"ConSpiracy-Coin-\",\"CNL\":\"ConcealCoin\",\"RAIN\":\"Condensate\",\"CFD\":\"Confido\",\"CJT\":\"ConnectJob-Token\",\"CQST\":\"ConquestCoin\",\"CNN\":\"Content-Neutrality-Network\",\"CUZ\":\"Cool-Cousin\",\"COOL\":\"CoolCoin\",\"CCX\":\"CoolDarkCoin\",\"XCPO\":\"Copico\",\"CLR\":\"CopperLark\",\"CORAL\":\"CoralPay\",\"CORE\":\"Core-Group-Asset\",\"COR\":\"Corion\",\"CTXC\":\"Cortex\",\"CSMIC\":\"Cosmic\",\"CMOS\":\"Cosmo\",\"ATOM*\":\"Cosmos\",\"CMC\":\"CosmosCoin\",\"COU\":\"Couchain\",\"XCP\":\"CounterParty\",\"CHT\":\"Countinghouse-Fund\",\"COV*\":\"CovenCoin\",\"COV\":\"Covesting\",\"CRAB\":\"CrabCoin\",\"CRACK\":\"CrackCoin\",\"CRC*\":\"CraftCoin\",\"CRAFT\":\"Craftcoin\",\"CFTY\":\"Crafty\",\"CRAIG\":\"CraigsCoin\",\"CRNK\":\"CrankCoin\",\"CRAVE*\":\"Crave-NG\",\"CRAVE\":\"CraveCoin\",\"CZC\":\"Crazy-Coin\",\"CRM\":\"Cream\",\"XCRE\":\"Creatio\",\"CREA\":\"CreativeChain\",\"CRDNC\":\"Credence-Coin\",\"CRB\":\"Creditbit-\",\"CRE*\":\"Creditcoin\",\"CRE\":\"Credits\",\"CRDS\":\"Credits\",\"CS*\":\"Credits\",\"CFT*\":\"Credo\",\"CREDO\":\"Credo\",\"CREVA\":\"Creva-Coin\",\"CROAT\":\"Croat\",\"CMCT\":\"Crowd-Machine\",\"CRC***\":\"CrowdCoin\",\"CCOS\":\"CrowdCoinage\",\"YUP\":\"Crowdholding\",\"WIZ\":\"Crowdwiz\",\"CRW\":\"Crown-Coin\",\"CRC**\":\"CryCash\",\"CRYPT\":\"CryptCoin\",\"CPT\":\"Cryptaur\",\"CRL\":\"Cryptelo-Coin\",\"CRPT\":\"Crypterium\",\"XCR\":\"Crypti\",\"CTO\":\"Crypto\",\"CESC\":\"Crypto-Escudo\",\"CIF\":\"Crypto-Improvement-Fund\",\"TKT\":\"Crypto-Tickets\",\"CWIS\":\"Crypto-Wisdom-Coin\",\"CWX\":\"Crypto-X\",\"C20\":\"Crypto20\",\"CABS\":\"CryptoABS\",\"BUK\":\"CryptoBuk\",\"CBX\":\"CryptoBullion\",\"CCRB\":\"CryptoCarbon\",\"CIRC\":\"CryptoCircuits\",\"FCS\":\"CryptoFocus\",\"CFT\":\"CryptoForecast\",\"CHBR\":\"CryptoHub\",\"TKR\":\"CryptoInsight\",\"CJ\":\"CryptoJacks\",\"CJC\":\"CryptoJournal\",\"LEU\":\"CryptoLEU\",\"CPAY\":\"CryptoPay\",\"CRPS\":\"CryptoPennies\",\"PING\":\"CryptoPing\",\"CS\":\"CryptoSpots\",\"CWV\":\"CryptoWave\",\"CWXT\":\"CryptoWorldXToken\",\"CDX*\":\"Cryptodex\",\"CGA\":\"Cryptographic-Anomaly\",\"CYT\":\"Cryptokenz\",\"CIX\":\"Cryptonetix\",\"CNX\":\"Cryptonex\",\"XCN\":\"Cryptonite\",\"CEFS\":\"CryptopiaFeeShares\",\"CRS\":\"Cryptoreal\",\"MN\":\"Cryptsy-Mining-Contract\",\"POINTS\":\"Cryptsy-Points\",\"CRTM\":\"Cryptum\",\"CVCOIN\":\"Crypviser\",\"CCT\":\"Crystal-Clear-Token-\",\"AUTO\":\"Cube\",\"QBT\":\"Cubits\",\"CTKN\":\"Curaizon\",\"CURE\":\"Curecoin\",\"CRU\":\"Curium\",\"XCS\":\"CybCSec-Coin\",\"CC\":\"CyberCoin\",\"CMT*\":\"CyberMiles\",\"CABS*\":\"CyberTrust\",\"CVT\":\"CyberVein\",\"CYDER\":\"Cyder-Coin\",\"CYG\":\"Cygnus\",\"CYP\":\"CypherPunkCoin\",\"FUNK\":\"Cypherfunks-Coin\",\"DAC\":\"DACash\",\"DADI\":\"DADI\",\"BET*\":\"DAO.casino\",\"GEN\":\"DAOstack\",\"DAS\":\"DAS\",\"DATX\":\"DATx\",\"DRP\":\"DCORP\",\"DESI\":\"DESI\",\"DFS\":\"DFSCoin\",\"DIM\":\"DIMCOIN\",\"DIW\":\"DIWtoken\",\"DMT\":\"DMarket\",\"DNN\":\"DNN-Token\",\"MTC\":\"DOCADEMIC\",\"DOVU\":\"DOVU\",\"DRPU\":\"DRP-Utility\",\"DRACO\":\"DT-Token\",\"DAI\":\"Dai\",\"DAN\":\"Daneel\",\"DAR\":\"Darcrus\",\"PROD\":\"Darenta\",\"DEC\":\"Darico\",\"DARK\":\"Dark\",\"DISK\":\"Dark-Lisk\",\"MOOND\":\"Dark-Moon\",\"DB\":\"DarkBit\",\"DRKC\":\"DarkCash\",\"DCC\":\"DarkCrave\",\"DETH\":\"DarkEther\",\"DGDC\":\"DarkGold\",\"DKC\":\"DarkKnightCoin\",\"DANK\":\"DarkKush\",\"DSB\":\"DarkShibe\",\"DT\":\"DarkToken\",\"DRKT\":\"DarkTron\",\"DNET\":\"Darknet\",\"DASC\":\"DasCoin\",\"DASH\":\"Dash\",\"DSH\":\"Dashcoin\",\"DTA\":\"Data\",\"DTT*\":\"Data-Trading\",\"DTX\":\"DataBroker-DAO\",\"DXT\":\"DataWallet\",\"DTB\":\"Databits\",\"DTC*\":\"Datacoin\",\"DTRC\":\"Datarius\",\"DAT\":\"Datum\",\"DAV\":\"DavorCoin\",\"DAXX\":\"DaxxCoin\",\"DTC\":\"DayTrader-Coin\",\"DHT\":\"DeHedge-Token\",\"XNA\":\"DeOxyRibose\",\"DBC*\":\"Debit-Coin\",\"DBTC\":\"DebitCoin\",\"DEB\":\"Debitum-Token\",\"DCT\":\"Decent\",\"DBET\":\"Decent.bet\",\"MANA\":\"Decentraland\",\"DML\":\"Decentralized-Machine-Learning\",\"DUBI\":\"Decentralized-Universal-Basic-Income\",\"HST\":\"Decision-Token\",\"DCR\":\"Decred\",\"DEEP\":\"Deep-Gold\",\"DBC\":\"DeepBrain-Chain\",\"ONION\":\"DeepOnion\",\"DEA\":\"Degas-Coin\",\"DEI\":\"Deimos\",\"DKD\":\"Dekado\",\"DPAY\":\"DelightPay\",\"DCRE\":\"DeltaCredits\",\"DNR\":\"Denarius\",\"DNO\":\"Denaro\",\"DENT\":\"Dent\",\"DCN\":\"Dentacoin\",\"DFBT\":\"DentalFix\",\"DERO\":\"Dero\",\"DSR\":\"Desire\",\"DES\":\"Destiny\",\"DTCT\":\"DetectorToken\",\"DTH\":\"Dether\",\"DVC\":\"DevCoin\",\"EVE\":\"Devery\",\"DEV\":\"Deviant-Coin\",\"DMD\":\"Diamond\",\"DCK\":\"DickCoin\",\"DIGS\":\"Diggits\",\"DGB\":\"DigiByte\",\"DGC\":\"DigiCoin\",\"CUBE\":\"DigiCube\",\"DEUR\":\"DigiEuro\",\"DIGIF\":\"DigiFel\",\"DGM\":\"DigiMoney\",\"DGPT\":\"DigiPulse\",\"DGMS\":\"Digigems\",\"DPP\":\"Digital-Assets-Power-Play\",\"DBG\":\"Digital-Bullion-Gold\",\"DDF\":\"Digital-Developers-Fund\",\"DRS\":\"Digital-Rupees\",\"XDN\":\"DigitalNote-\",\"DP\":\"DigitalPrice\",\"WAGE\":\"Digiwage\",\"DGD\":\"Digix-DAO\",\"DGX\":\"Digix-Gold-token\",\"DIG\":\"Dignity\",\"DIME\":\"DimeCoin\",\"DCY\":\"Dinastycoin\",\"DIN\":\"Dinero\",\"XDQ\":\"Dirac-Coin\",\"DCC*\":\"Distributed-Credit-Chain\",\"DIT\":\"Ditcoin\",\"DIVX\":\"Divi\",\"DTC**\":\"DivotyCoin\",\"DXC\":\"DixiCoin\",\"DLISK\":\"Dlisk\",\"NOTE\":\"Dnotes\",\"DOC\":\"Doc-Coin\",\"NRN\":\"Doc.ai-Neuron\",\"DOCK\":\"Dock.io\",\"DOGED\":\"DogeCoinDark\",\"DGORE\":\"DogeGoreCoin\",\"XDP\":\"DogeParty\",\"DOGE\":\"Dogecoin\",\"DLC\":\"DollarCoin\",\"DLR\":\"DollarOnline\",\"DRT\":\"DomRaider\",\"DON\":\"DonationCoin\",\"DOPE\":\"DopeCoin\",\"DOR\":\"Dorado\",\"DOT\":\"Dotcoin\",\"BOAT\":\"Doubloon\",\"Dow\":\"DowCoin\",\"DRA\":\"DraculaCoin\",\"DFT\":\"Draftcoin\",\"DRG\":\"Dragon-Coin\",\"XDB\":\"DragonSphere\",\"DRGN\":\"Dragonchain\",\"DRM8\":\"Dream8Coin\",\"DREAM\":\"DreamTeam-Token\",\"DRZ\":\"Droidz\",\"DRC\":\"Dropcoin\",\"DROP\":\"Dropil\",\"DRXNE\":\"Droxne\",\"DUB\":\"DubCoin\",\"DBIC\":\"DubaiCoin\",\"DBIX\":\"DubaiCoin\",\"DUCK\":\"DuckDuckCoin\",\"DUTCH\":\"Dutch-Coin\",\"DUX\":\"DuxCoin\",\"DYN\":\"Dynamic\",\"DTR\":\"Dynamic-Trading-Rights\",\"DTEM\":\"Dystem\",\"DBR\":\"Düber\",\"ECC*\":\"E-CurrencyCoin\",\"EDR\":\"E-Dinar-Coin\",\"EFL\":\"E-Gulden\",\"EB3\":\"EB3coin\",\"EBC\":\"EBCoin\",\"ECC\":\"ECC\",\"ECO\":\"ECOcoin\",\"EDRC\":\"EDRCoin\",\"EGO\":\"EGOcoin\",\"EJAC\":\"EJA-Coin\",\"ELTCOIN\":\"ELTCOIN\",\"ENTRC\":\"ENTER-COIN\",\"EOS\":\"EOS\",\"EPIK\":\"EPIK-Token\",\"EQL\":\"EQUAL\",\"EQ\":\"EQUI\",\"EQUI\":\"EQUI-Token\",\"ERB\":\"ERBCoin\",\"ETS\":\"ETH-Share\",\"EGAS\":\"ETHGAS\",\"EUNO\":\"EUNO\",\"EXRN\":\"EXRNchain\",\"EZC\":\"EZCoin\",\"EZM\":\"EZMarket\",\"EZT\":\"EZToken\",\"EA\":\"EagleCoin\",\"EAGS\":\"EagsCoin\",\"EARTH\":\"Earth-Token\",\"EAC\":\"EarthCoin\",\"EMT\":\"EasyMine\",\"ETKN\":\"EasyToken\",\"EBZ\":\"Ebitz\",\"EBS\":\"EbolaShare\",\"EKO\":\"EchoLink\",\"EC\":\"Eclipse\",\"ECOB\":\"EcoBit\",\"EDDIE\":\"Eddie-coin\",\"EDGE\":\"EdgeCoin\",\"EDG\":\"Edgeless\",\"LEDU\":\"Education-Ecosystem\",\"EDU\":\"Educoin\",\"EDC\":\"EducoinV\",\"EGG\":\"EggCoin\",\"EGT\":\"Egretia\",\"EDO\":\"Eidoo\",\"EMC2\":\"Einsteinium\",\"ELC\":\"Elacoin\",\"XEL\":\"Elastic\",\"ELA\":\"Elastos\",\"ECA\":\"Electra\",\"ELEC\":\"Electrify.Asia\",\"ETN\":\"Electroneum\",\"EKN\":\"Elektron\",\"ELE\":\"Elementrem\",\"ELM\":\"Elements\",\"ELI*\":\"Elicoin\",\"ELI\":\"Eligma\",\"ELIX\":\"Elixir\",\"ELLA\":\"Ellaism\",\"ELP\":\"Ellerium\",\"ELLI\":\"ElliotCoin\",\"ELT\":\"Eloplay\",\"ELY\":\"Elysian\",\"ELS\":\"Elysium\",\"AEC*\":\"EmaratCoin\",\"EMB\":\"EmberCoin\",\"MBRS\":\"Embers\",\"EMD\":\"Emerald\",\"EMC\":\"Emercoin\",\"EMN\":\"Eminent-Token-\",\"EMIGR\":\"EmiratesGoldCoin\",\"EPY*\":\"Emphy\",\"EMPC\":\"EmporiumCoin\",\"EPY\":\"Empyrean\",\"DNA\":\"EncrypGen\",\"ETT\":\"EncryptoTel\",\"EDR*\":\"Endor-Protocol-Token-\",\"ENE\":\"EneCoin\",\"ETK\":\"Energi-Token\",\"TSL\":\"Energo\",\"ENRG\":\"EnergyCoin\",\"EGCC\":\"Engine\",\"XNG\":\"Enigma\",\"ENG\":\"Enigma\",\"ENJ\":\"Enjin-Coin\",\"ENK\":\"Enkidu\",\"ENTER\":\"EnterCoin-(ENTER)\",\"ENTRP\":\"Entropy-Token\",\"ENU\":\"Enumivo\",\"EVN\":\"Envion\",\"EQUAL\":\"EqualCoin\",\"EQT\":\"EquiTrader\",\"EQB\":\"Equibit\",\"EQM\":\"Equilibrium-Coin\",\"EFYT\":\"Ergo\",\"ERT*\":\"Eristica\",\"ERO\":\"Eroscoin\",\"ERR\":\"ErrorCoin\",\"ERY\":\"Eryllium\",\"ESP\":\"Espers\",\"ERT\":\"Esports.com\",\"ESS\":\"Essentia\",\"XEC\":\"Eternal-Coin\",\"XET\":\"Eternal-Token\",\"ENT\":\"Eternity\",\"EBET\":\"EthBet\",\"ETBS\":\"EthBits\",\"LEND\":\"EthLend\",\"ETHB\":\"EtherBTC\",\"EDT\":\"EtherDelta\",\"DOGETH\":\"EtherDoge\",\"ETL\":\"EtherLite\",\"ESZ\":\"EtherSportz\",\"ETZ\":\"EtherZero\",\"ECH\":\"EthereCash\",\"ETH\":\"Ethereum\",\"ETBT\":\"Ethereum-Black\",\"BLUE\":\"Ethereum-Blue\",\"ECASH\":\"Ethereum-Cash\",\"ETC\":\"Ethereum-Classic\",\"ETHD\":\"Ethereum-Dark\",\"ETG\":\"Ethereum-Gold\",\"ETHM\":\"Ethereum-Meta\",\"EMV\":\"Ethereum-Movie-Venture\",\"ETHPR\":\"Ethereum-Premium\",\"LNK\":\"Ethereum.Link\",\"BTCE\":\"EthereumBitcoin\",\"ETF\":\"EthereumFog\",\"ELITE\":\"EthereumLite\",\"ETHS\":\"EthereumScrypt\",\"RIYA\":\"Etheriya\",\"DICE\":\"Etheroll\",\"FUEL\":\"Etherparty\",\"ESC\":\"Ethersportcoin\",\"NEC*\":\"Ethfinex-Nectar-Token\",\"HORSE\":\"Ethorse-\",\"ETHOS\":\"Ethos\",\"ET4\":\"Eticket4\",\"EUC\":\"Eurocoin\",\"ERC\":\"EuropeCoin\",\"EVN*\":\"EvenCoin\",\"EVENT\":\"Event-Token\",\"EVC\":\"Eventchain\",\"EGC\":\"EverGreenCoin\",\"EVX\":\"Everex\",\"IQ\":\"Everipedia\",\"EVR\":\"Everus\",\"EOC\":\"EveryonesCoin\",\"EVIL\":\"EvilCoin\",\"EXB\":\"ExaByte-(EXB)\",\"XUC\":\"Exchange-Union\",\"EXCC\":\"ExchangeCoin\",\"EXN\":\"ExchangeN\",\"EXCL\":\"Exclusive-Coin\",\"EXE\":\"ExeCoin\",\"EXC\":\"Eximchain\",\"EXIT\":\"ExitCoin\",\"EXP\":\"Expanse\",\"XP\":\"Experience-Points\",\"EXY\":\"Experty\",\"EON\":\"Exscudo\",\"EXTN\":\"Extensive-Coin\",\"XTRA\":\"ExtraCredit\",\"XSB\":\"Extreme-Sportsbook\",\"XT\":\"ExtremeCoin\",\"F16\":\"F16Coin\",\"FARM\":\"FARM-Coin\",\"FX\":\"FCoin\",\"FIBRE\":\"FIBRE\",\"eFIC\":\"FIC-Network\",\"FLASH\":\"FLASH-coin\",\"FLIK\":\"FLiK\",\"FLM\":\"FOLM-coin\",\"FREE\":\"FREE-coin\",\"FT\":\"Fabric-Token\",\"FC\":\"Facecoin\",\"FACE\":\"Faceter-\",\"FCT\":\"Factoids\",\"FAIR\":\"FairCoin\",\"FAIR*\":\"FairGame\",\"FAME\":\"FameCoin\",\"XFT\":\"Fantasy-Cash\",\"FCN\":\"FantomCoin-\",\"FRD\":\"Farad\",\"FST\":\"FastCoin\",\"DROP*\":\"FaucetCoin\",\"FAZZ\":\"FazzCoin\",\"FTC\":\"FeatherCoin\",\"TIPS\":\"FedoraCoin\",\"FIL\":\"FileCoin\",\"FILL\":\"Fillit\",\"FNTB\":\"FinTab\",\"FIND\":\"FindCoin\",\"FIN\":\"Finom-FIN-Token\",\"NOM\":\"Finom-NOM-Token\",\"FTX\":\"FintruX\",\"FIRE\":\"FireCoin\",\"FLOT\":\"FireLotto\",\"FRC\":\"FireRoosterCoin\",\"FFC\":\"FireflyCoin\",\"1ST\":\"FirstBlood\",\"FIRST\":\"FirstCoin\",\"FRST\":\"FirstCoin\",\"FIST\":\"FistBump\",\"FIT\":\"Fitcoin\",\"FRV\":\"Fitrova\",\"FLAP\":\"Flappy-Coin\",\"FLX\":\"Flash\",\"FLVR\":\"FlavorCoin\",\"FNP\":\"FlipNpik\",\"FLIXX\":\"Flixxo\",\"FLO\":\"FlorinCoin\",\"FLT\":\"FlutterCoin\",\"FLUZ\":\"FluzFluz\",\"FLY\":\"FlyCoin\",\"FYP\":\"FlypMe\",\"FLDC\":\"Folding-Coin\",\"FLLW\":\"Follow-Coin\",\"FNO\":\"Fonero\",\"FONZ\":\"FonzieCoin\",\"FOOD\":\"FoodCoin\",\"FOPA\":\"Fopacoin\",\"FOR\":\"Force-Coin\",\"XFC\":\"Forever-Coin\",\"FOREX\":\"ForexCoin\",\"FOTA\":\"Fortuna\",\"FSBT\":\"Forty-Seven-Bank\",\"FOXT\":\"Fox-Trading\",\"FRAC\":\"FractalCoin\",\"FRN\":\"Francs\",\"FRK\":\"Franko\",\"FRWC\":\"Frankywillcoin\",\"FRAZ\":\"FrazCoin\",\"FGZ\":\"Free-Game-Zone\",\"FRE\":\"FreeCoin\",\"FREC\":\"Freyrchain\",\"FSC\":\"FriendshipCoin\",\"FDZ\":\"Friendz\",\"FUCK\":\"Fuck-Token\",\"FC2\":\"Fuel2Coin\",\"FJC\":\"FujiCoin\",\"NTO\":\"Fujinto\",\"FLS\":\"Fuloos-Coin\",\"FUNC\":\"FunCoin\",\"FUN\":\"FunFair\",\"FUND\":\"Fund-Platform\",\"FND\":\"FundRequest\",\"FYN\":\"FundYourselfNow\",\"FSN*\":\"Fusion\",\"FSN\":\"Fusion\",\"FUTC\":\"FutCoin\",\"FTP\":\"FuturePoints\",\"FTW\":\"FutureWorks\",\"FTO\":\"FuturoCoin\",\"FXT\":\"FuzeX\",\"FUZZ\":\"Fuzzballs\",\"GAIA\":\"GAIA-Platform\",\"GAKH\":\"GAKHcoin\",\"GAT\":\"GATCOIN\",\"GBRC\":\"GBR-Coin\",\"GTO\":\"GIFTO\",\"GIN\":\"GINcoin\",\"GIZ\":\"GIZMOcoin\",\"GMC*\":\"GMC-Coin\",\"GPU\":\"GPU-Coin\",\"GSM\":\"GSM-Coin\",\"GXS\":\"GXChain\",\"GNR\":\"Gainer\",\"ORE\":\"Galactrum\",\"GES\":\"Galaxy-eSolutions\",\"GLX\":\"GalaxyCoin\",\"GAM\":\"Gambit-coin\",\"GMCN\":\"GambleCoin\",\"GTC\":\"Game\",\"GBT\":\"GameBetCoin\",\"GML\":\"GameLeagueCoin\",\"UNITS\":\"GameUnits\",\"GX\":\"GameX\",\"GAME\":\"Gamecredits\",\"FLP\":\"Gameflip\",\"GNJ\":\"GanjaCoin-V2\",\"GAP\":\"Gapcoin\",\"GRLC\":\"Garlicoin\",\"GAS\":\"Gas\",\"GEM\":\"Gems\",\"GEMZ\":\"Gemz-Social\",\"GXC*\":\"GenXCoin\",\"GNX\":\"Genaro-Network\",\"GVT\":\"Genesis-Vision\",\"XGS\":\"GenesisX\",\"GSY\":\"GenesysCoin\",\"GEN*\":\"Genstake\",\"GEO\":\"GeoCoin\",\"GUNS\":\"GeoFunders\",\"GER\":\"GermanCoin\",\"SPKTR\":\"Ghost-Coin\",\"GHC\":\"GhostCoin\",\"GHOUL\":\"Ghoul-Coin\",\"GIC\":\"Giant\",\"GIFT\":\"GiftNet\",\"GFT\":\"Giftcoin\",\"GIG\":\"GigCoin\",\"GHS*\":\"Giga-Hash\",\"WTT\":\"Giga-Watt\",\"GGS\":\"Gilgam\",\"GIM\":\"Gimli\",\"GMR\":\"Gimmer\",\"GOT\":\"Giotto-Coin\",\"GIVE\":\"GiveCoin\",\"GLA\":\"Gladius\",\"GLOBE\":\"Global\",\"GCR\":\"Global-Currency-Reserve\",\"GJC\":\"Global-Jobcoin\",\"GSC\":\"Global-Social-Chain\",\"GTC*\":\"Global-Tour-Coin\",\"BSTY\":\"GlobalBoost\",\"GLC\":\"GlobalCoin\",\"GLT\":\"GlobalToken\",\"GSI\":\"Globex-SCI\",\"GSX\":\"GlowShares\",\"GLYPH\":\"GlyphCoin\",\"GNO\":\"Gnosis\",\"xGOx\":\"Go!\",\"GBX\":\"GoByte\",\"GO\":\"GoChain\",\"GOA\":\"GoaCoin\",\"GOAL\":\"Goal-Bonanza\",\"GOAT\":\"Goat\",\"GPL\":\"Gold-Pressed-Latinum\",\"GRX\":\"Gold-Reward-Token\",\"GB\":\"GoldBlocks\",\"GLD\":\"GoldCoin\",\"MNTP\":\"GoldMint\",\"GP\":\"GoldPieces\",\"XGR\":\"GoldReserve\",\"GEA\":\"Goldea\",\"XGB\":\"GoldenBird\",\"GMX\":\"Goldmaxcoin\",\"GNT\":\"Golem-Network-Token\",\"GOLF\":\"GolfCoin\",\"GOLOS\":\"Golos\",\"GBG\":\"Golos-Gold\",\"GOOD\":\"GoodCoin\",\"GOOD*\":\"Goodomy\",\"GOON\":\"Goonies\",\"BUCKS*\":\"GorillaBucks\",\"GST\":\"Gostcoin\",\"GOTX\":\"GothicCoin\",\"GRFT\":\"Graft-Blockchain\",\"GDC\":\"GrandCoin\",\"GAI\":\"GraphGrail-AI\",\"GRAV\":\"Graviton\",\"GBIT\":\"GravityBit\",\"GRE\":\"GreenCoin\",\"GRMD\":\"GreenMed\",\"GEX\":\"GreenX\",\"GREXIT\":\"GrexitCoin\",\"GRID\":\"Grid+\",\"GRC\":\"GridCoin\",\"GRM\":\"GridMaster\",\"GRID*\":\"GridPay\",\"GMC\":\"Gridmaster\",\"GRS\":\"Groestlcoin-\",\"GRO\":\"Gron-Digital\",\"GRWI\":\"Growers-International\",\"GROW\":\"GrownCoin\",\"GRW\":\"GrowthCoin\",\"GET\":\"Guaranteed-Entrance-Token\",\"GETX\":\"Guaranteed-Ethurance-Token-Extra\",\"GCC\":\"GuccioneCoin\",\"GUE\":\"GuerillaCoin\",\"NLG\":\"Gulden\",\"GUN\":\"GunCoin\",\"GUP\":\"Guppy\",\"GXC\":\"Gx-Coin\",\"HELL\":\"HELL-COIN\",\"PLAY\":\"HEROcoin\",\"HOLD\":\"HOLD\",\"HQX\":\"HOQU\",\"HODL\":\"HOdlcoin\",\"HTML\":\"HTML-Coin\",\"HTML5\":\"HTML5-Coin\",\"HKN\":\"Hacken\",\"HKG\":\"Hacker-Gold\",\"HAC\":\"Hackspace-Capital\",\"HADE\":\"Hade-Token\",\"HALAL\":\"Halal\",\"HLC\":\"Halal-Chain\",\"HAL\":\"Halcyon\",\"HALLO\":\"Halloween-Coin\",\"HALO\":\"Halo-Platform\",\"HMT\":\"Hamster-Marketplace-Token\",\"HAMS\":\"HamsterCoin\",\"HION\":\"Handelion\",\"HPC\":\"HappyCoin\",\"HCC\":\"HappyCreatorCoin-\",\"HRB\":\"Harbour-DAO\",\"HSC\":\"HashCoin-\",\"HGS\":\"HashGains\",\"XHV\":\"Haven-Protocol\",\"HAV\":\"Havven\",\"HAT\":\"Hawala.Today\",\"HZT\":\"HazMatCoin\",\"HAZE\":\"HazeCoin\",\"HHEM\":\"Healthureum\",\"WORM\":\"HealthyWorm\",\"HB\":\"HeartBout\",\"HEAT\":\"Heat-Ledger\",\"HVC\":\"HeavyCoin\",\"HDG\":\"Hedge-Token\",\"HEDG\":\"Hedgecoin\",\"HEEL\":\"HeelCoin\",\"HYS\":\"Heiss-Shares\",\"HBZ\":\"Helbiz\",\"HNC\":\"Hellenic-Coin\",\"HGT\":\"Hello-Gold\",\"HMP\":\"HempCoin\",\"HERO\":\"Hero\",\"HER\":\"Hero-Node\",\"HEX\":\"HexCoin\",\"HXT\":\"HextraCoin\",\"HXX\":\"HexxCoin\",\"HMC\":\"Hi-Mutual-Society\",\"XHI\":\"HiCoin\",\"HPB\":\"High-Performance-Blockchain\",\"HVCO\":\"High-Voltage-Coin\",\"AIMS\":\"HighCastle-Token\",\"HIRE\":\"HireMatch\",\"HFT\":\"Hirefreehands\",\"HTC\":\"Hitcoin\",\"HIVE\":\"Hive\",\"HVN\":\"Hive-Project\",\"HBN\":\"HoboNickels\",\"HWC\":\"HollyWoodCoin\",\"HOT*\":\"Holo\",\"HBC\":\"HomeBlockCoin\",\"HONEY\":\"Honey\",\"HZ\":\"Horizon\",\"HSP\":\"Horse-Power\",\"HYT\":\"HoryouToken\",\"HSR\":\"Hshare\",\"HBT\":\"Hubii-Network\",\"HMQ\":\"Humaniq\",\"HNC*\":\"Huncoin\",\"HUC\":\"HunterCoin\",\"HT\":\"Huobi-Token\",\"HUR\":\"Hurify\",\"HUSH\":\"Hush\",\"HOT\":\"Hydro-Protocol\",\"HYDRO\":\"Hydrogen\",\"H2O\":\"Hydrominer\",\"HYPER\":\"HyperCoin\",\"HYP\":\"Hyperstake\",\"IHT\":\"I-House-Token\",\"I0C\":\"I0coin\",\"ICASH\":\"ICASH\",\"ICOO\":\"ICO-OpenLedger\",\"ICOS\":\"ICOBox\",\"ICX\":\"ICON-Project\",\"ICST\":\"ICST\",\"IDXM\":\"IDEX-Membership\",\"ILC\":\"ILCoin\",\"ILCT\":\"ILCoin-Token\",\"IML\":\"IMMLA\",\"INS\":\"INS-Ecosystem\",\"IOC\":\"IOCoin\",\"IOST\":\"IOS-token\",\"IOTA\":\"IOTA\",\"IOU\":\"IOU1\",\"IPSX\":\"IP-Exchange\",\"IPC*\":\"IPChain\",\"IRC\":\"IRONCOIN\",\"IXC\":\"IXcoin\",\"ROCK\":\"Ice-Rock-Mining\",\"ICB\":\"IceBergCoin\",\"ICOB\":\"Icobid\",\"ICON\":\"Iconic\",\"ICN\":\"Iconomi\",\"IGNIS\":\"Ignis\",\"IC\":\"Ignition\",\"REX\":\"Imbrex\",\"IMV\":\"ImmVRse\",\"IMX\":\"Impact\",\"IMPCH\":\"Impeach\",\"IPC\":\"ImperialCoin\",\"IMPS\":\"Impulse-Coin\",\"IN\":\"InCoin\",\"INPAY\":\"InPay\",\"NKA\":\"IncaKoin\",\"INCNT\":\"Incent\",\"INCP\":\"InceptionCoin\",\"INC\":\"Incrementum\",\"IDH\":\"IndaHash\",\"IMS\":\"Independent-Money-System\",\"ERC20\":\"Index-ERC20\",\"INDI\":\"IndiCoin\",\"IND\":\"Indorse\",\"IFX\":\"Infinex\",\"IFC\":\"Infinite-Coin\",\"XIN\":\"Infinity-Economics\",\"INF8\":\"Infinium-8\",\"IFLT\":\"InflationCoin\",\"INTO\":\"Influ-Token\",\"INFX\":\"Influxcoin\",\"INK\":\"Ink\",\"XNK\":\"Ink-Protocol\",\"ILK\":\"Inlock\",\"$OUND\":\"Inmusik\",\"INN\":\"Innova\",\"INSN\":\"Insane-Coin\",\"INSANE\":\"InsaneCoin\",\"WOLF\":\"Insanity-Coin\",\"INSTAR\":\"Insights-Network\",\"ICC\":\"Insta-Cash-Coin\",\"MINE\":\"Instamine-Nuggets\",\"INSUR\":\"InsurChain-Coin\",\"IPL\":\"InsurePal\",\"IQB\":\"Intelligence-Quotient-Benefit\",\"ITT\":\"Intelligent-Trading\",\"ITNS\":\"IntenseCoin\",\"XID*\":\"International-Diamond-Coin\",\"INT\":\"Internet-Node-Token\",\"IOP\":\"Internet-of-People\",\"INXT\":\"Internxt\",\"HOLD*\":\"Interstellar-Holdings\",\"ITZ\":\"Interzone\",\"INV*\":\"Invacio\",\"IFT\":\"InvestFeed\",\"INV\":\"Invictus\",\"IVZ\":\"InvisibleCoin\",\"INVOX\":\"Invox-Finance\",\"ITC\":\"IoT-Chain\",\"IOTX\":\"IoTeX-Network\",\"ION\":\"Ionomy\",\"IRL\":\"IrishCoin\",\"ISL\":\"IslaCoin\",\"ITA\":\"Italocoin\",\"ING\":\"Iungo\",\"IEC\":\"IvugeoEvolutionCoin\",\"IVY\":\"IvyKoin\",\"IWT\":\"IwToken\",\"J8T\":\"JET8\",\"JEX\":\"JEX-Token\",\"JIO\":\"JIO-Token\",\"JOY*\":\"JOYSO\",\"JPC*\":\"JackPotCoin\",\"JANE\":\"JaneCoin\",\"JNS\":\"Janus\",\"JVY\":\"Javvy\",\"JC\":\"JesusCoin\",\"JET\":\"Jetcoin\",\"JWL\":\"Jewels\",\"JNT\":\"Jibrel-Network-Token\",\"JIF\":\"JiffyCoin\",\"JCR\":\"Jincor\",\"JINN\":\"Jinn\",\"JOBS\":\"JobsCoin\",\"J\":\"JoinCoin\",\"JOINT\":\"Joint-Ventures\",\"JOK\":\"JokerCoin\",\"XJO\":\"JouleCoin\",\"JOY\":\"JoyToken\",\"JUDGE\":\"JudgeCoin\",\"JBS\":\"JumBucks-Coin\",\"JUMP\":\"Jumpcoin\",\"JKC\":\"JunkCoin\",\"JDC\":\"JustDatingSite\",\"KAAS\":\"KAASY.AI\",\"KAT\":\"KATZcoin\",\"KEC\":\"KEYCO\",\"KRC\":\"KRCoin\",\"KREDS\":\"KREDS\",\"KWH\":\"KWHCoin\",\"KZC\":\"KZCash\",\"KLKS\":\"Kalkulus\",\"KAPU\":\"Kapu\",\"KBC\":\"Karatgold-coin\",\"KRB\":\"Karbo\",\"KRM\":\"Karma\",\"KARMA\":\"Karmacoin\",\"KAYI\":\"Kayı\",\"KEK\":\"KekCoin\",\"KEN\":\"Kencoin\",\"KEP\":\"Kepler\",\"KC\":\"Kernalcoin\",\"KETAN\":\"Ketan\",\"KEX\":\"KexCoin\",\"KEY*\":\"KeyCoin\",\"KICK\":\"KickCoin\",\"KLC\":\"KiloCoin\",\"KIN\":\"Kin\",\"KIND\":\"Kind-Ads\",\"KING\":\"King93\",\"KNC**\":\"KingN-Coin\",\"MEOW\":\"Kittehcoin\",\"KED\":\"Klingon-Empire-Darsek\",\"KDC\":\"Klondike-Coin\",\"KNW\":\"Knowledge-\",\"KOBO\":\"KoboCoin\",\"KOLION\":\"Kolion\",\"KMD\":\"Komodo\",\"KORE\":\"Kore\",\"KRAK\":\"Kraken\",\"KRONE\":\"Kronecoin\",\"KGC\":\"KrugerCoin\",\"KRL\":\"Kryll\",\"KTK\":\"KryptCoin\",\"KR\":\"Krypton\",\"KBR\":\"Kubera-Coin\",\"KUBO\":\"KubosCoin\",\"KCS\":\"Kucoin\",\"KURT\":\"Kurrent\",\"KUSH\":\"KushCoin\",\"KNC\":\"Kyber-Network\",\"LA\":\"LATOKEN\",\"LBC\":\"LBRY-Credits\",\"LEO\":\"LEOcoin\",\"LGBTQ\":\"LGBTQoin\",\"LHC\":\"LHCoin\",\"LIFE\":\"LIFE\",\"LIPC\":\"LIpcoin\",\"LTBC\":\"LTBCoin\",\"LUX\":\"LUXCoin\",\"LALA\":\"LaLa-World\",\"LAB\":\"Labrys\",\"BAC*\":\"LakeBanker\",\"TAU\":\"Lamden-Tau\",\"PIX\":\"Lampix\",\"LANA\":\"LanaCoin\",\"LTH\":\"Lathaan\",\"LAT\":\"Latium\",\"LATX\":\"LatiumX\",\"LAZ\":\"Lazarus\",\"LEPEN\":\"LePenCoin\",\"LEA\":\"LeaCoin\",\"LDC\":\"LeadCoin\",\"LEAF\":\"LeafCoin\",\"LGD*\":\"Legendary-Coin\",\"LGD\":\"Legends-Cryptocurrency\",\"LGO\":\"Legolas-Exchange\",\"LELE\":\"Lelecoin\",\"LEMON\":\"LemonCoin\",\"LCT\":\"LendConnect\",\"LND\":\"Lendingblock\",\"LOAN\":\"Lendoit\",\"LST\":\"Lendroid-Support-Token\",\"LENIN\":\"LeninCoin\",\"LIR\":\"Let-it-Ride\",\"LVL*\":\"LevelNet-Token\",\"LVG\":\"Leverage-Coin\",\"LEV\":\"Leverj\",\"XLC\":\"LeviarCoin\",\"XLB\":\"LibertyCoin\",\"LBA\":\"Libra-Credit\",\"LXC\":\"LibrexCoin\",\"LIGER\":\"Ligercoin\",\"LSD\":\"LightSpeedCoin\",\"LIKE\":\"LikeCoin\",\"LIMX\":\"LimeCoinX\",\"LTD\":\"Limited-Coin\",\"LINDA\":\"Linda\",\"LET\":\"LinkEye\",\"LNC*\":\"Linker-Coin\",\"LINX\":\"Linx\",\"LQD\":\"Liquid\",\"LSK\":\"Lisk\",\"LTCC\":\"Listerclassic-Coin\",\"LBTC\":\"LiteBitcoin\",\"LTG\":\"LiteCoin-Gold\",\"LTCU\":\"LiteCoin-Ultra\",\"LCWP\":\"LiteCoinW-Plus\",\"LTCR\":\"LiteCreed\",\"LDOGE\":\"LiteDoge\",\"LTB\":\"Litebar-\",\"LTC\":\"Litecoin\",\"LTCH\":\"Litecoin-Cash\",\"LCP\":\"Litecoin-Plus\",\"LCASH\":\"LitecoinCash\",\"LCC\":\"LitecoinCash\",\"LTCD\":\"LitecoinDark\",\"LTCX\":\"LitecoinX\",\"LTS\":\"Litestar-Coin\",\"LTA\":\"Litra\",\"LIVE\":\"Live-Stars\",\"LIV\":\"LiviaCoin\",\"LIZ\":\"Lizus-Payment\",\"LWF\":\"Local-World-Forwarders\",\"LCS\":\"LocalCoinSwap\",\"LOCI\":\"LociCoin\",\"LOC*\":\"LockTrip\",\"LOC\":\"Loco\",\"LGR\":\"Logarithm\",\"LOKI\":\"Loki\",\"LMC\":\"LomoCoin\",\"LOOK\":\"LookCoin\",\"LOOM\":\"Loom-Network\",\"LRC\":\"Loopring\",\"LOT\":\"LottoCoin\",\"LYK\":\"Loyakk-Vega\",\"LYL\":\"LoyalCoin\",\"BASH\":\"LuckChain\",\"LCK\":\"Luckbox\",\"LK7\":\"Lucky7Coin\",\"LUCKY\":\"LuckyBlocks\",\"LKY\":\"LuckyCoin\",\"LDM\":\"Ludum-token\",\"LUN\":\"Lunyr\",\"LC\":\"Lutetium-Coin\",\"LUX**\":\"Luxmi-Coin\",\"LYC\":\"LycanCoin\",\"LDN\":\"Lydiancoin\",\"LKK\":\"Lykke\",\"LYM\":\"Lympo\",\"LYNX\":\"Lynx\",\"LYB\":\"LyraBar\",\"MRK\":\"MARK.SPACE\",\"MCAP\":\"MCAP\",\"MCV\":\"MCV-Token\",\"MIS\":\"MIScoin\",\"MMNXT\":\"MMNXT-\",\"MMO\":\"MMOCoin\",\"MMXVI\":\"MMXVI\",\"MOS\":\"MOS-Coin\",\"MUN\":\"MUNcoin\",\"MUSD\":\"MUSDcoin\",\"YCE\":\"MYCE\",\"MAC\":\"MachineCoin\",\"MCRN\":\"MacronCoin\",\"MRV\":\"Macroverse\",\"MDC*\":\"MadCoin\",\"ART\":\"Maecenas\",\"MAG**\":\"Maggie-Token\",\"MGN\":\"MagnaCoin\",\"MAG\":\"Magnet\",\"MAG*\":\"Magos\",\"MAID\":\"MaidSafe-Coin\",\"MMXIV\":\"MaieutiCoin\",\"MFT\":\"Mainframe\",\"MSC*\":\"MaisCoin\",\"MIV\":\"MakeItViral\",\"MKR\":\"Maker\",\"MAT*\":\"Manet-Coin\",\"MANNA\":\"Manna\",\"MAPC\":\"MapCoin\",\"MAR\":\"MarijuanaCoin\",\"MASP\":\"Market.space\",\"MRS\":\"MarsCoin\",\"MARS\":\"MarsCoin-\",\"MXT\":\"MartexCoin\",\"MARV\":\"Marvelous\",\"MARX\":\"MarxCoin\",\"MARYJ\":\"MaryJane-Coin\",\"MSR\":\"Masari\",\"MC\":\"Mass-Coin\",\"MASS\":\"Mass.Cloud\",\"MGD\":\"MassGrid\",\"MCAR\":\"MasterCar\",\"MSC\":\"MasterCoin\",\"MM\":\"MasterMint\",\"MTR\":\"MasterTraderCoin\",\"MAN*\":\"Matrix-AI-Network\",\"MTX\":\"Matryx\",\"MAX\":\"MaxCoin\",\"MYC\":\"MayaCoin\",\"MZC\":\"MazaCoin\",\"MBIT\":\"Mbitbooks\",\"MLITE\":\"MeLite\",\"MDT*\":\"Measurable-Data-Token-\",\"MED*\":\"MediBloc\",\"MEDI\":\"MediBond\",\"MCU\":\"MediChain\",\"MDS\":\"MediShares\",\"MNT*\":\"Media-Network-Coin\",\"MPT\":\"Media-Protocol-Token\",\"MEDX\":\"Mediblock\",\"MDC\":\"MedicCoin\",\"MEDIC\":\"MedicCoin\",\"MTN*\":\"Medicalchain\",\"MED\":\"MediterraneanCoin\",\"MPRO\":\"MediumProject\",\"MEC\":\"MegaCoin\",\"MEGA\":\"MegaFlash\",\"XMS\":\"Megastake\",\"MLN\":\"Melon\",\"MET\":\"Memessenger\",\"MMC\":\"MemoryCoin\",\"MRN\":\"Mercoin\",\"MVP\":\"Merculet\",\"MER\":\"Mercury\",\"GMT\":\"Mercury-Protocol\",\"MTL\":\"Metal\",\"MTLM3\":\"Metal-Music-v3\",\"METAL\":\"MetalCoin\",\"ETP\":\"Metaverse\",\"MET*\":\"Metronome\",\"AMM\":\"MicroMoney\",\"MDX\":\"Midex\",\"MDT\":\"Midnight\",\"MUU\":\"MilkCoin\",\"MIL\":\"Milllionaire-Coin\",\"MILO\":\"MiloCoin\",\"MNC\":\"MinCoin\",\"MG\":\"Mind-Gene\",\"MND\":\"MindCoin\",\"MIC\":\"Mindexcoin\",\"MINT*\":\"Mineable-Token\",\"MIO\":\"Miner-One-token\",\"MIN\":\"Minerals-Coin\",\"MNE\":\"Minereum\",\"MRT\":\"MinersReward\",\"MNM\":\"Mineum\",\"MINEX\":\"Minex\",\"MNX\":\"MinexCoin\",\"MAT\":\"MiniApps\",\"MNTS\":\"Mint\",\"MINT\":\"MintCoin\",\"MITH\":\"Mithril\",\"XIN*\":\"Mixin\",\"CHF*\":\"MobileBridge-Momentum\",\"EMGO\":\"MobileGo\",\"MGO\":\"MobileGo\",\"MOBI\":\"Mobius\",\"MTRC\":\"ModulTrade\",\"MDL*\":\"Modulum\",\"MOD\":\"Modum\",\"MDA\":\"Moeda\",\"MOIN\":\"MoinCoin\",\"MOJO\":\"Mojocoin\",\"TAB\":\"MollyCoin\",\"MONA\":\"MonaCoin\",\"MCO\":\"Monaco\",\"MNZ\":\"Monaize\",\"XMR\":\"Monero\",\"ZMR\":\"Monero-0\",\"XMC\":\"Monero-Classic\",\"XMRG\":\"Monero-Gold\",\"XMO\":\"Monero-Original\",\"XMV\":\"MoneroV\",\"MONETA\":\"Moneta\",\"MCN\":\"MonetaVerde\",\"MUE\":\"MonetaryUnit\",\"MTH\":\"Monetha\",\"MNB\":\"MoneyBag\",\"MONEY\":\"MoneyCoin\",\"MRP*\":\"MoneyRebel\",\"MNY\":\"Monkey\",\"MONK\":\"Monkey-Project\",\"XMCC\":\"Monoeci\",\"MBI\":\"Monster-Byte-Inc\",\"MBLC\":\"Mont-Blanc\",\"MOON\":\"MoonCoin\",\"MITX\":\"Morpheus-Infrastructure-Token\",\"MRPH\":\"Morpheus-Network\",\"MRP\":\"MorpheusCoin\",\"MZX\":\"Mosaic-Network\",\"MOAT\":\"Mother-Of-All-Tokens\",\"MSP\":\"Mothership\",\"XMN\":\"Motion\",\"MTN**\":\"Motion\",\"MOTO\":\"Motocoin\",\"MTK\":\"Moya-Token\",\"MRSA\":\"MrsaCoin\",\"MUDRA\":\"MudraCoin\",\"MLT\":\"MultiGames\",\"MWC\":\"MultiWallet-Coin\",\"MBT\":\"Multibot\",\"MRY\":\"MurrayCoin\",\"MUSE\":\"Muse\",\"MUSIC\":\"Musicoin\",\"MCI\":\"Musiconomi\",\"MST\":\"MustangCoin\",\"MUT\":\"Mutual-Coin\",\"MBC\":\"My-Big-Coin\",\"MYB\":\"MyBit\",\"MT*\":\"MyToken\",\"WISH\":\"MyWish\",\"MT\":\"Mycelium-Token\",\"XMY\":\"MyriadCoin\",\"MYST\":\"Mysterium\",\"MYST*\":\"MysteryCoin\",\"NANJ\":\"NANJCOIN\",\"XEM\":\"NEM\",\"NEO\":\"NEO\",\"NEOG\":\"NEO-Gold\",\"NEXO\":\"NEXO\",\"NOX\":\"NITRO\",\"NIX\":\"NIX\",\"NKN\":\"NKN\",\"NOAH\":\"NOAHCOIN\",\"CHFN\":\"NOKU-CHF\",\"EURN\":\"NOKU-EUR\",\"NOKU\":\"NOKU-Master-token\",\"NPC\":\"NPCcoin\",\"NVST\":\"NVO\",\"NXE\":\"NXEcoin\",\"NXTI\":\"NXTI\",\"NXTTY\":\"NXTTY\",\"NYX\":\"NYXCOIN\",\"NFN\":\"Nafen\",\"NGC\":\"NagaCoin\",\"NKT\":\"NakomotoDark\",\"NMC\":\"Namecoin\",\"NAMO\":\"NamoCoin\",\"NANO\":\"Nano\",\"NAN\":\"NanoToken\",\"NPX\":\"Napoleon-X\",\"NAS2\":\"Nas2Coin\",\"NAUT\":\"Nautilus-Coin\",\"NAV\":\"NavCoin\",\"NAVI\":\"NaviAddress\",\"NEBL\":\"Neblio\",\"NEBU\":\"Nebuchadnezzar\",\"NBAI\":\"Nebula-AI\",\"NAS\":\"Nebulas\",\"NDC*\":\"NeedleCoin\",\"NEF\":\"NefariousCoin\",\"NEC\":\"NeoCoin\",\"NEOS\":\"NeosCoin\",\"NTCC\":\"NeptuneClassic\",\"NBIT\":\"NetBit\",\"NET\":\"NetCoin\",\"NTM\":\"NetM\",\"NETKO\":\"Netko\",\"NTWK\":\"Network-Token\",\"NETC\":\"NetworkCoin\",\"NEU*\":\"NeuCoin\",\"NEU\":\"Neumark\",\"NRO\":\"Neuro\",\"NRC\":\"Neurocoin\",\"NTK\":\"Neurotoken\",\"NTRN\":\"Neutron\",\"NEVA\":\"NevaCoin\",\"NDC\":\"NeverDie\",\"NIC\":\"NewInvestCoin\",\"NYC\":\"NewYorkCoin\",\"NZC\":\"NewZealandCoin\",\"NEWB\":\"Newbium\",\"NCP\":\"Newton-Coin\",\"NXC\":\"Nexium\",\"NEXT\":\"Next.exchange-Token\",\"NXS\":\"Nexus\",\"NICE\":\"NiceCoin\",\"NIHL\":\"Nihilo-Coin\",\"NMB\":\"Nimbus-Coin\",\"NIMFA\":\"Nimfamoney\",\"NET*\":\"Nimiq-Exchange-Token\",\"NTC\":\"NineElevenTruthCoin\",\"NDOGE\":\"NinjaDoge\",\"NBR\":\"Niobio-Cash\",\"NBC\":\"Niobium\",\"NLC\":\"NoLimitCoin\",\"NLC2\":\"NoLimitCoin\",\"NOBL\":\"NobleCoin\",\"NODE\":\"Node\",\"NRB\":\"NoirBits\",\"NRS\":\"NoirShares\",\"NOO\":\"Noocoin\",\"NVC\":\"NovaCoin\",\"NWCN\":\"NowCoin\",\"NBX\":\"Noxbox\",\"NBT\":\"NuBits\",\"NSR\":\"NuShares\",\"NUBIS\":\"NubisCoin\",\"NCASH\":\"Nucleus-Vision\",\"NUKE\":\"NukeCoin\",\"NKC\":\"Nukecoinz\",\"NLX\":\"Nullex\",\"NULS\":\"Nuls\",\"N7\":\"Number7\",\"NUM\":\"NumbersCoin\",\"NMR\":\"Numeraire\",\"XNC*\":\"Numismatic-Collections\",\"NMS\":\"Numus\",\"NXT\":\"Nxt\",\"NYAN\":\"NyanCoin\",\"NBL\":\"Nybble\",\"ODE\":\"ODEM-\",\"ODMC\":\"ODMCoin\",\"OK\":\"OKCash\",\"OKOIN\":\"OKOIN\",\"OPC\":\"OP-Coin\",\"OPP*\":\"OPP-Open-WiFi\",\"ORS\":\"ORS-SA\",\"OBITS\":\"Obits-Coin\",\"OBS\":\"Obscurebay\",\"ODN\":\"Obsidian\",\"OCL\":\"Oceanlab\",\"OTX\":\"Octanox\",\"OCTO*\":\"OctoBit-Coin\",\"OCTO\":\"OctoCoin\",\"OCN\":\"Odyssey\",\"ODNT\":\"Old-Dogs-New-Tricks\",\"OLDSF\":\"OldSafeCoin\",\"OLV\":\"OldV\",\"OLYMP\":\"OlympCoin\",\"MOT\":\"Olympus-Labs\",\"OMA\":\"OmegaCoin\",\"OMGC\":\"OmiseGO-Classic\",\"OMG\":\"OmiseGo\",\"OMNI\":\"Omni\",\"OMC\":\"OmniCron\",\"ONL\":\"On.Live\",\"OLT\":\"OneLedger\",\"RNT\":\"OneRoot-Network\",\"ONX\":\"Onix\",\"OIO\":\"Online\",\"ONT\":\"Ontology\",\"XPO\":\"Opair\",\"OPAL\":\"OpalCoin\",\"OPEN\":\"Open-Platform\",\"OTN\":\"Open-Trading-Network\",\"OAX\":\"OpenANX\",\"OSC\":\"OpenSourceCoin\",\"ZNT\":\"OpenZen\",\"OPES\":\"Opes\",\"OPP\":\"Opporty\",\"OSA\":\"Optimal-Shelf-Availability-Token\",\"OPTION\":\"OptionCoin\",\"OPT\":\"Opus\",\"OCT\":\"OracleChain\",\"OC\":\"OrangeCoin\",\"ORB\":\"Orbitcoin\",\"RDC\":\"Ordocoin\",\"ORGT\":\"Organic-Token\",\"ORI\":\"Origami\",\"TRAC\":\"OriginTrail\",\"OCC\":\"Original-Crypto-Coin\",\"ORLY\":\"OrlyCoin\",\"ORME\":\"Ormeus-Coin\",\"ORO\":\"OroCoin\",\"OROC\":\"Orocrypt\",\"OS76\":\"OsmiumCoin\",\"OWD\":\"Owlstand\",\"ZXC\":\"Oxcert\",\"OXY\":\"Oxycoin\",\"PRL\":\"Oyster-Pearl\",\"OYS\":\"Oyster-Platform\",\"SHL\":\"Oyster-Shell\",\"GENE\":\"PARKGENE\",\"PAT\":\"PATRON\",\"PAXEX\":\"PAXEX\",\"PQT\":\"PAquarium\",\"PAI\":\"PCHAIN\",\"PHI\":\"PHI-Token\",\"PITCH\":\"PITCH\",\"PLNC\":\"PLNCoin\",\"PROUD\":\"PROUD-Money\",\"PF\":\"PROVER\",\"PSI\":\"PSIcoin\",\"PWR\":\"PWR-Coin\",\"PX\":\"PXcoin\",\"PCS\":\"Pabyosi-Coin\",\"PBC\":\"PabyosiCoin\",\"$PAC\":\"PacCoin\",\"PAK\":\"Pakcoin\",\"PLMT\":\"Pallium\",\"PND\":\"PandaCoin\",\"PINKX\":\"PantherCoin\",\"PAN\":\"Pantos\",\"PRP\":\"Papyrus\",\"PRG\":\"Paragon\",\"DUO\":\"ParallelCoin\",\"PARA\":\"ParanoiaCoin\",\"PARETO\":\"Pareto-Network-Token\",\"PKB\":\"ParkByte\",\"PAR\":\"Parlay\",\"PART\":\"Particl\",\"PASC\":\"Pascal-Coin\",\"PASL\":\"Pascal-Lite\",\"PAS\":\"Passive-Coin\",\"PTOY\":\"Patientory\",\"PAVO\":\"Pavocoin\",\"XPY\":\"PayCoin\",\"PYC\":\"PayCoin\",\"PFR\":\"PayFair\",\"PAYP\":\"PayPeer\",\"PPP\":\"PayPie\",\"PYP\":\"PayPro\",\"PYN\":\"Paycentos\",\"CON_\":\"Paycon\",\"PMNT\":\"Paymon\",\"PYT\":\"Payther\",\"PEC\":\"PeaceCoin\",\"XPB\":\"Pebble-Coin\",\"PCL\":\"Peculium\",\"PCO\":\"Pecunio\",\"PCN\":\"PeepCoin\",\"PPC\":\"PeerCoin\",\"GUESS\":\"Peerguess\",\"PPY\":\"Peerplays\",\"PGC\":\"Pegascoin\",\"PEN*\":\"PenCoin\",\"PNT\":\"Penta\",\"PTA\":\"PentaCoin\",\"PNY\":\"Peony-Coin\",\"MAN\":\"People\",\"MEME\":\"Pepe\",\"PEPECASH\":\"Pepe-Cash\",\"PIE\":\"Persistent-Information-Exchange\",\"PERU\":\"PeruCoin\",\"PTC\":\"PesetaCoin\",\"PSB\":\"PesoBit\",\"PTR\":\"Petro\",\"XPD\":\"PetroDollar\",\"PXL\":\"Phalanx\",\"SOUL*\":\"Phantasma\",\"PNX\":\"PhantomX\",\"XPH\":\"PharmaCoin\",\"PHS\":\"PhilosophersStone\",\"PXC\":\"PhoenixCoin\",\"PHR*\":\"Phore\",\"PHO\":\"Photon\",\"PHR\":\"Phreak\",\"PGN\":\"Pigeoncoin\",\"PIGGY\":\"Piggy-Coin\",\"PKC\":\"Pikciochain\",\"PLR\":\"Pillar\",\"PINK\":\"PinkCoin\",\"PCOIN\":\"Pioneer-Coin\",\"PIO\":\"Pioneershares\",\"SKULL\":\"Pirate-Blocks\",\"PIRL\":\"Pirl\",\"PIZZA\":\"PizzaCoin\",\"PLANET\":\"PlanetCoin\",\"PLC*\":\"PlatinCoin\",\"PNC\":\"PlatiniumCoin\",\"XPTX\":\"PlatinumBAR\",\"LUC\":\"Play-2-Live\",\"PKT\":\"Playkey\",\"PLX\":\"PlexCoin\",\"PLURA\":\"PluraCoin\",\"PLC\":\"PlusCoin\",\"PLUS1\":\"PlusOneCoin\",\"PTC**\":\"Plutocoin\",\"PLU\":\"Pluton\",\"POE\":\"Po.et\",\"POS\":\"PoSToken\",\"POA\":\"Poa-Network\",\"XPS\":\"PoisonIvyCoin\",\"XPOKE\":\"PokeChain\",\"POKER\":\"PokerCoin\",\"XPST\":\"PokerSports\",\"PAL\":\"PolicyPal-Network\",\"POLIS\":\"PolisPay\",\"POLY\":\"PolyBit\",\"NCT\":\"PolySwarm\",\"PLBT\":\"Polybius\",\"POLY*\":\"Polymath-Network\",\"XSP\":\"PoolStamp\",\"POP\":\"PopularCoin\",\"PPT\":\"Populous\",\"PEX\":\"PosEx\",\"PSD\":\"Poseidon\",\"POSQ\":\"Poseidon-Quark\",\"TRON\":\"Positron\",\"POST\":\"PostCoin\",\"POT\":\"PotCoin\",\"POWR\":\"Power-Ledger\",\"PRE\":\"Premium\",\"PRE*\":\"Presearch\",\"HILL\":\"President-Clinton\",\"PRES\":\"President-Trump\",\"PBT\":\"Primalbase\",\"PST\":\"Primas\",\"PXI\":\"Prime-X1\",\"PRIME\":\"PrimeChain\",\"XPM\":\"PrimeCoin\",\"PRX\":\"Printerium\",\"PRM\":\"PrismChain\",\"PIVX\":\"Private-Instant-Verified-Transaction\",\"PRIX\":\"Privatix\",\"PZM\":\"Prizm\",\"XPRO\":\"ProCoin\",\"PROC\":\"ProCurrency\",\"PCM\":\"Procom\",\"PHC\":\"Profit-Hunters-Coin\",\"PDC\":\"Project-Decorum\",\"JTX\":\"Project-J\",\"PAI*\":\"Project-Pai\",\"OMX\":\"Project-Shivom\",\"ZEPH\":\"Project-Zephyr\",\"PRFT\":\"Proof-Suite-Token\",\"PROPS\":\"Props\",\"PTC*\":\"Propthereum\",\"PRO\":\"Propy\",\"VRP\":\"Prosense.tv\",\"PGL\":\"Prospectors\",\"PRC\":\"ProsperCoin\",\"PROTON\":\"Proton\",\"PTS*\":\"Protoshares\",\"XES\":\"Proxeus\",\"PSEUD\":\"PseudoCash\",\"PSY\":\"Psilocybin\",\"PBL\":\"Publica\",\"PULSE\":\"Pulse\",\"PMA\":\"PumaPay\",\"NPXS\":\"Pundi-X\",\"PUPA\":\"PupaCoin\",\"PURA\":\"Pura\",\"PURE\":\"Pure\",\"VIDZ\":\"PureVidz\",\"PGT\":\"Puregold-token\",\"PURK\":\"Purk\",\"PRPS\":\"Purpose\",\"HLP\":\"Purpose-Coin\",\"PUSHI\":\"Pushi\",\"PUT\":\"PutinCoin\",\"PYLNT\":\"Pylon-Network\",\"QLC\":\"QLC-Chain\",\"QTUM\":\"QTUM\",\"QBT*\":\"Qbao\",\"QORA\":\"QoraCoin\",\"QBK\":\"QuBuck-Coin\",\"QSP\":\"Quantstamp\",\"QAU\":\"Quantum\",\"QRL\":\"Quantum-Resistant-Ledger\",\"Q1S\":\"Quantum1Net\",\"QKC\":\"QuarkChain\",\"QRK\":\"QuarkCoin\",\"QTZ\":\"Quartz\",\"QTL\":\"Quatloo\",\"QCN\":\"Quazar-Coin\",\"Q2C\":\"QubitCoin\",\"QBC\":\"Quebecoin\",\"QSLV\":\"Quicksilver-coin\",\"QUN\":\"QunQun\",\"QASH\":\"Quoine-Liquid\",\"XQN\":\"Quotient\",\"QVT\":\"Qvolta\",\"QWARK\":\"Qwark\",\"QWC\":\"Qwertycoin\",\"RFL\":\"RAFL\",\"KRX\":\"RAVN-Korrax-\",\"RAC\":\"RAcoin\",\"RHOC\":\"RChain\",\"RCN*\":\"RCoin\",\"REAL\":\"REAL\",\"REBL\":\"REBL\",\"MWAT\":\"RED-MegaWatt\",\"RST\":\"REGA-Risk-Sharing-Token\",\"REM\":\"REMME\",\"RGC\":\"RG-Coin\",\"ROI\":\"ROIcoin\",\"ROS\":\"ROS-Coin\",\"RADI\":\"RadicalCoin\",\"RADS\":\"Radium\",\"RDN\":\"RadonPay\",\"RDN*\":\"Raiden-Network\",\"RAP\":\"Rapture\",\"RTE\":\"Rate3-\",\"XRA\":\"Ratecoin\",\"RATIO\":\"Ratio\",\"RAVE\":\"Ravelous\",\"RVN\":\"Ravencoin\",\"RZR\":\"RazorCoin\",\"RCT\":\"RealChain\",\"REA\":\"Realisto\",\"RCC\":\"Reality-Clash\",\"RRT\":\"Recovery-Right-Tokens\",\"RPX\":\"Red-Pulse\",\"RCX\":\"RedCrowCoin\",\"RED\":\"Redcoin\",\"RDD\":\"Reddcoin\",\"REDN\":\"Reden\",\"REE\":\"ReeCoin\",\"REF\":\"RefToken\",\"RFR\":\"Refereum\",\"REC\":\"Regalcoin\",\"RLX\":\"Relex\",\"REL\":\"Reliance\",\"RPM\":\"Render-Payment\",\"RNDR\":\"Render-Token\",\"RNS\":\"RenosCoin\",\"BERRY\":\"Rentberry\",\"REPO\":\"Repo-Coin\",\"REN\":\"Republic-Token\",\"REPUX\":\"Repux\",\"REQ\":\"Request-Network\",\"RMS\":\"Resumeo-Shares\",\"RBIT\":\"ReturnBit\",\"RNC\":\"ReturnCoin\",\"R\":\"Revain\",\"REV\":\"Revenu\",\"RVR\":\"Revolution-VR\",\"XRE\":\"RevolverCoin\",\"RHEA\":\"Rhea\",\"XRL\":\"Rialto.AI\",\"RBR\":\"Ribbit-Rewards\",\"RICE\":\"RiceCoin\",\"RIDE\":\"Ride-My-Car\",\"RIC\":\"Riecoin\",\"RBT\":\"Rimbit\",\"RING\":\"RingCoin\",\"RIPO\":\"RipOffCoin\",\"RCN\":\"Ripio\",\"RIPT\":\"RiptideCoin\",\"RBX\":\"RiptoBuX\",\"RISE\":\"Rise\",\"RVT\":\"Rivetz\",\"RAC**\":\"RoBET\",\"PUT*\":\"Robin8-Profile-Utility-Token\",\"RAC*\":\"RoboAdvisorCoin\",\"ROX\":\"Robotina\",\"RKT\":\"Rock-Token\",\"ROK\":\"Rockchain\",\"ROCK*\":\"RocketCoin-\",\"RPC\":\"RonPaulCoin\",\"ROOT\":\"RootCoin\",\"ROOTS\":\"RootProject\",\"RT2\":\"RotoCoin\",\"ROUND\":\"RoundCoin\",\"ROE\":\"Rover-Coin\",\"RKC\":\"Royal-Kingdom-Coin\",\"RYC\":\"RoyalCoin\",\"ROYAL\":\"RoyalCoin\",\"RYCN\":\"RoyalCoin-2.0\",\"RBIES\":\"Rubies\",\"RUBY\":\"Rubius\",\"RUBIT\":\"Rublebit\",\"RBY\":\"RubyCoin\",\"RUFF\":\"Ruff\",\"RUPX\":\"Rupaya\",\"RUP\":\"Rupee\",\"RC\":\"Russiacoin\",\"RMC\":\"Russian-Mining-Coin\",\"RUST\":\"RustCoin\",\"RUSTBITS\":\"Rustbits\",\"RYO\":\"Ryo\",\"S8C\":\"S88-Coin\",\"SABR\":\"SABR-Coin\",\"SAR*\":\"SARCoin\",\"XSH\":\"SHIELD\",\"SMNX\":\"SMNX\",\"SNM\":\"SONM\",\"SXDT\":\"SPECTRE-Dividend-Token\",\"SXUT\":\"SPECTRE-Utility-Token\",\"SPICE\":\"SPiCE-Venture-Capital-\",\"SSV\":\"SSVCoin\",\"STAC\":\"STAC\",\"STEX\":\"STEX\",\"STK\":\"STK-Token\",\"STS\":\"STRESScoin\",\"XSTC\":\"Safe-Trade-Coin\",\"SAFE\":\"SafeCoin\",\"SAFEX\":\"SafeExchangeCoin\",\"SFE\":\"Safecoin\",\"SFR\":\"SaffronCoin\",\"SAF\":\"Safinus\",\"SAGA\":\"SagaCoin\",\"SFU\":\"Saifu\",\"SKB*\":\"Sakura-Bloom\",\"SKR\":\"Sakuracoin\",\"SAL\":\"SalPay\",\"SALT\":\"Salt-Lending\",\"SLS\":\"SaluS\",\"SMSR\":\"Samsara-Coin\",\"SND\":\"Sandcoin\",\"SAN\":\"Santiment\",\"SPN*\":\"Sapien-Network\",\"XAI\":\"SapienceCoin\",\"SAT\":\"Satisfaction-Token\",\"STV\":\"Sativa-Coin\",\"MAD*\":\"SatoshiMadness\",\"SAT2\":\"Saturn2Coin\",\"STO\":\"Save-The-Ocean\",\"SANDG\":\"Save-and-Gain\",\"SVD\":\"Savedroid\",\"SWC\":\"Scanetchain-Token\",\"SCOOBY\":\"Scooby-coin\",\"SCORE\":\"Scorecoin\",\"SCOR\":\"Scorista\",\"SCR*\":\"Scorum\",\"SCOT\":\"Scotcoin\",\"SCRL\":\"Scroll\",\"DDD\":\"Scry.info\",\"SCRPT\":\"ScryptCoin\",\"SCT\":\"ScryptToken\",\"SRT\":\"Scrypto\",\"SCRT\":\"SecretCoin\",\"SRC\":\"SecureCoin\",\"SEEDS\":\"SeedShares\",\"B2X\":\"SegWit2x\",\"SEL\":\"SelenCoin\",\"KEY\":\"SelfKey\",\"SSC\":\"SelfSell\",\"SEM\":\"Semux\",\"SDRN\":\"Senderon\",\"SNS\":\"Sense\",\"SENSE\":\"Sense-Token\",\"SEN\":\"Sentaro\",\"SENT\":\"Sentinel\",\"SENC\":\"Sentinel-Chain\",\"UPP\":\"Sentinel-Protocol\",\"SEQ\":\"Sequence\",\"SERA\":\"Seraph\",\"SRNT\":\"Serenity\",\"SET\":\"Setcoin\",\"SETH\":\"Sether\",\"SP\":\"Sex-Pistols\",\"SXC\":\"SexCoin\",\"SHA\":\"Shacoin\",\"SHADE\":\"ShadeCoin\",\"SDC\":\"ShadowCash\",\"SS\":\"Sharder\",\"SSS\":\"ShareChain\",\"SHR\":\"ShareRing\",\"SAK\":\"SharkCoin\",\"SHP*\":\"Sharpe-Capital\",\"JEW\":\"Shekel\",\"SHLD\":\"ShieldCoin\",\"SHIFT\":\"Shift\",\"SH\":\"Shilling\",\"SHIP\":\"ShipChain\",\"SHORTY\":\"ShortyCoin\",\"SHOW\":\"ShowCoin\",\"SHPING\":\"Shping-Coin\",\"SHREK\":\"ShrekCoin\",\"SC\":\"Siacoin\",\"SIB\":\"SibCoin\",\"SGL\":\"Sigil\",\"SIG\":\"Signal\",\"SGN\":\"Signals-Network\",\"SIGT\":\"Signatum\",\"SNTR\":\"Silent-Notary\",\"SILK\":\"SilkCoin\",\"OST\":\"Simple-Token\",\"SPLB\":\"SimpleBank\",\"SIGU\":\"Singular\",\"SNGLS\":\"SingularDTV\",\"AGI\":\"SingularityNET\",\"SRN\":\"SirinLabs\",\"SKC\":\"Skeincoin\",\"SKIN\":\"Skincoin\",\"SKRP\":\"Skraps\",\"SKR*\":\"Skrilla-Token\",\"SKM\":\"Skrumble-Network\",\"SKB\":\"SkullBuzz\",\"SKY\":\"Skycoin\",\"SLX\":\"Slate\",\"SLM\":\"SlimCoin\",\"SLING\":\"Sling-Coin\",\"SIFT\":\"Smart-Investment-Fund-Token\",\"SMART*\":\"SmartBillions\",\"SMART\":\"SmartCash\",\"SMC\":\"SmartCoin\",\"SLT\":\"SmartLands\",\"SMT*\":\"SmartMesh\",\"SMLY\":\"SmileyCoin\",\"SMF\":\"SmurfCoin\",\"SNIP\":\"SnipCoin\",\"SNOV\":\"Snovio\",\"XSG\":\"Snowgem\",\"SOAR\":\"Soarcoin\",\"SMAC\":\"Social-Media-Coin\",\"SMT\":\"Social-Media-Market\",\"SEND\":\"Social-Send\",\"SOCC\":\"SocialCoin\",\"XBOT\":\"SocialXbotCoin\",\"SCL\":\"Sociall\",\"SOIL\":\"SoilCoin\",\"SOJ\":\"Sojourn-Coin\",\"SOL\":\"Sola\",\"SDAO\":\"Solar-DAO\",\"SLR\":\"SolarCoin\",\"CELL\":\"SolarFarm\",\"SFC\":\"Solarflarecoin\",\"XLR\":\"Solaris\",\"SOLE\":\"SoleCoin\",\"SCT*\":\"Soma\",\"SONG\":\"Song-Coin\",\"SSD\":\"Sonic-Screw-Driver-Coin\",\"SOON\":\"SoonCoin\",\"SPHTX\":\"SophiaTX\",\"SNK\":\"Sosnovkino\",\"SOUL\":\"SoulCoin\",\"SPX\":\"Sp8de\",\"SCASH\":\"SpaceCash\",\"SPC*\":\"SpaceChain\",\"SPACE\":\"SpaceCoin\",\"SPA\":\"SpainCoin\",\"SPANK\":\"SpankChain\",\"SPK\":\"Sparks\",\"SPEC\":\"SpecCoin\",\"SPX*\":\"Specie\",\"XSPEC\":\"Spectre\",\"SPEND\":\"Spend\",\"SPHR\":\"Sphere-Coin\",\"XID\":\"Sphre-AIR\",\"SPC\":\"SpinCoin\",\"SPD*\":\"Spindle\",\"SPN\":\"Spoon\",\"SPORT\":\"SportsCoin\",\"SPF\":\"SportyCo\",\"SPT\":\"Spots\",\"SPOTS\":\"Spots\",\"SPR\":\"Spreadcoin\",\"SPRTS\":\"Sprouts\",\"SQP\":\"SqPay\",\"SQL\":\"Squall-Coin\",\"XSI\":\"Stability-Shares\",\"SBC\":\"StableCoin\",\"STCN\":\"Stakecoin\",\"XSN\":\"Stakenet\",\"STA*\":\"Stakers\",\"STHR\":\"Stakerush\",\"STALIN\":\"StalinCoin\",\"STC\":\"StarChain\",\"STR*\":\"StarCoin\",\"STAR*\":\"StarCoin\",\"SRC*\":\"StarCredits\",\"STT\":\"Staramba\",\"STAR\":\"Starbase\",\"START\":\"StartCoin\",\"STA\":\"Starta\",\"STP\":\"StashPay\",\"SQOIN\":\"StasyQ\",\"SNT\":\"Status-Network-Token\",\"STAX\":\"Staxcoin\",\"XST\":\"StealthCoin\",\"PNK\":\"SteamPunk\",\"STEEM\":\"Steem\",\"SBD*\":\"Steem-Backed-Dollars\",\"XLM\":\"Stellar\",\"XTL\":\"Stellite\",\"STN\":\"Steneum-Coin\",\"STEPS\":\"Steps\",\"SLG\":\"SterlingCoin\",\"SPD\":\"Stipend\",\"STOCKBET\":\"StockBet\",\"SCC\":\"StockChain-Coin\",\"STQ\":\"Storiqa-Token\",\"STORJ\":\"Storj\",\"SJCX\":\"StorjCoin\",\"STORM\":\"Storm\",\"STX\":\"Stox\",\"STAK\":\"Straks\",\"SISA\":\"Strategic-Investments-in-Significant-Areas\",\"STRAT\":\"Stratis\",\"SSH\":\"StreamSpace\",\"DATA\":\"Streamr-DATAcoin\",\"SHND\":\"StrongHands\",\"SUB*\":\"Subscriptio\",\"SUB\":\"Substratum-Network\",\"SUCR\":\"Sucre\",\"SGR\":\"Sugar-Exchange\",\"SUMO\":\"Sumokoin\",\"SNC\":\"SunContract\",\"SSTC\":\"SunShotCoin\",\"SUP\":\"Supcoin\",\"SBTC\":\"Super-Bitcoin\",\"SUPER\":\"SuperCoin\",\"UNITY\":\"SuperNET\",\"M1\":\"SupplyShock\",\"SPM\":\"Supreme\",\"RMT\":\"SureRemit\",\"SUR\":\"Suretly\",\"SCX\":\"Swachhcoin\",\"BUCKS\":\"SwagBucks\",\"SWT\":\"Swarm-City-Token\",\"SWM\":\"Swarm-Fund\",\"SWARM\":\"SwarmCoin\",\"SWEET\":\"SweetStake\",\"SWFTC\":\"SwftCoin\",\"SWING\":\"SwingCoin\",\"SCN\":\"Swiscoin\",\"CHSB\":\"SwissBorg\",\"SRC**\":\"SwissRealCoin\",\"SIC\":\"Swisscoin\",\"SWTH\":\"Switcheo\",\"SDP\":\"SydPakCoin\",\"SYNC\":\"SyncCoin\",\"MFG\":\"SyncFab\",\"SYC\":\"SynchroCoin\",\"SYNX\":\"Syndicate\",\"AMP\":\"Synereo\",\"SNRG\":\"Synergy\",\"SYS\":\"SysCoin\",\"TBT\":\"T-BOT\",\"BAR\":\"TBIS-token\",\"TDFB\":\"TDFB\",\"TFD\":\"TE-FOOD\",\"TKY\":\"THEKEY-Token\",\"TOA\":\"TOA-Coin\",\"TPC\":\"TPCash\",\"XTROPTIONS\":\"TROPTIONS\",\"TAG\":\"TagCoin\",\"TAJ\":\"TajCoin\",\"TAK\":\"TakCoin\",\"TKLN\":\"Taklimakan\",\"TAM\":\"TamaGucci\",\"XTO\":\"Tao\",\"TTT\":\"Tap-Project\",\"TAP\":\"TappingCoin\",\"TGT\":\"TargetCoin\",\"TAT\":\"Tatiana-Coin\",\"TSE\":\"TattooCoin\",\"TEC\":\"TeCoin\",\"TEAM\":\"TeamUP\",\"TECH\":\"TechCoin\",\"THS\":\"TechShares\",\"TEK\":\"TekCoin\",\"TEL\":\"Telcoin\",\"GRAM\":\"Telegram-Open-Network\",\"TELL\":\"Tellurion\",\"PAY\":\"TenX\",\"TENNET\":\"Tennet\",\"TERN\":\"Ternio\",\"TRN\":\"Ternion\",\"TRC\":\"TerraCoin\",\"TER\":\"TerraNovaCoin\",\"TESLA\":\"TeslaCoilCoin\",\"TES\":\"TeslaCoin\",\"USDT\":\"Tether\",\"TRA\":\"Tetra\",\"XTZ\":\"Tezos\",\"THNX\":\"ThankYou\",\"0xDIARY\":\"The-0xDiary-Token\",\"ABYSS\":\"The-Abyss\",\"EFX\":\"The-EFFECT-Network\",\"TFC\":\"The-Freedom-Coin\",\"THC\":\"The-Hempcoin\",\"XVE\":\"The-Vegan-Initiative\",\"CHIEF\":\"TheChiefCoin\",\"GCC*\":\"TheGCCcoin\",\"VIG\":\"TheVig\",\"TCR\":\"Thecreed\",\"MAY\":\"Theresa-May-Coin\",\"THETA\":\"Theta\",\"TAGR\":\"Think-And-Get-Rich-Coin\",\"THRT\":\"ThriveToken\",\"TSC\":\"ThunderStake\",\"TIA\":\"Tianhe\",\"TDX\":\"Tidex-Token\",\"TNT\":\"Tierion\",\"TIE\":\"Ties-Network\",\"TGC\":\"TigerCoin\",\"TIG\":\"Tigereum\",\"XTC\":\"TileCoin\",\"TIME\":\"Time\",\"TNB\":\"Time-New-Bank\",\"TME\":\"Timereum\",\"TMC\":\"TimesCoin\",\"TIO*\":\"Tio-Tour-Guides\",\"TIP\":\"Tip-Blockchain\",\"TIT\":\"TitCoin\",\"TBAR\":\"Titanium-BAR\",\"TTC\":\"TittieCoin\",\"TMT*\":\"ToTheMoon\",\"TODAY\":\"TodayCoin\",\"TAAS\":\"Token-as-a-Service\",\"TKN\":\"TokenCard\",\"TCT\":\"TokenClub-\",\"TDS\":\"TokenDesk\",\"TPAY*\":\"TokenPay\",\"ACE\":\"TokenStars\",\"TBX\":\"Tokenbox\",\"TEN\":\"Tokenomy\",\"TKS\":\"Tokes\",\"TKA\":\"Tokia\",\"TOK\":\"TokugawaCoin\",\"TOKC\":\"Tokyo-Coin\",\"TOM\":\"Tomahawkcoin\",\"TOMO\":\"TomoChain\",\"TOR\":\"TorCoin\",\"TOT\":\"TotCoin\",\"BBC\":\"TraDove\",\"MTN\":\"TrackNetToken\",\"TRCT\":\"Tracto\",\"TIO\":\"Trade.io\",\"TDZ\":\"Tradelize\",\"TRAK\":\"TrakInvest\",\"TX\":\"Transfer\",\"TBCX\":\"TrashBurn\",\"TRV\":\"Travel-Coin\",\"TT\":\"TravelChain\",\"TRF\":\"Travelflex\",\"TMT**\":\"Traxia-Membership-Token\",\"TZC\":\"TrezarCoin\",\"TRIA\":\"Triaconta\",\"TRI\":\"Triangles-Coin\",\"TRIBE\":\"TribeToken\",\"TRICK\":\"TrickyCoin\",\"TRDT\":\"Trident\",\"TRIG\":\"Trigger\",\"TNC\":\"Trinity-Network-Credit\",\"TRIP\":\"Trippki\",\"TRVC\":\"Trivecoin\",\"TRW\":\"Triwer\",\"TPG\":\"Troll-Payment\",\"TPAY\":\"TrollPlay\",\"TKN*\":\"TrollTokens\",\"TROLL\":\"Trollcoin\",\"TRX\":\"Tronix\",\"TRK\":\"TruckCoin\",\"TRCK\":\"Truckcoin\",\"TFL\":\"True-Flip-Lottery\",\"TUSD\":\"True-USD\",\"TDP\":\"TrueDeck\",\"TGAME\":\"TrueGame\",\"TIC\":\"TrueInvestmentCoin\",\"TRUMP\":\"TrumpCoin\",\"TRST\":\"TrustCoin\",\"TRUST\":\"TrustPlus\",\"TLP\":\"TulipCoin\",\"TUR\":\"Turron\",\"TRTL\":\"TurtleCoin\",\"TUT\":\"Tutellus\",\"TWLV\":\"Twelve-Coin\",\"TWIST\":\"TwisterCoin\",\"UUU\":\"U-Network\",\"UCASH\":\"U.CASH\",\"UCN\":\"UC-Coin\",\"UCT\":\"UCOT\",\"UFO\":\"UFO-Coin\",\"XUP\":\"UPcoin\",\"UR\":\"UR\",\"USDC\":\"USCoin\",\"USOAMIC\":\"USOAMIC\",\"UBC\":\"Ubcoin\",\"UBEX\":\"Ubex\",\"UBQ\":\"Ubiq\",\"UBIQ\":\"Ubiqoin\",\"U\":\"Ucoin\",\"USC\":\"Ultimate-Secure-Cash\",\"UTC\":\"UltraCoin\",\"XUN\":\"UltraNote\",\"ULTC\":\"Umbrella\",\"UMC\":\"Umbrella-Coin\",\"UNC\":\"UnCoin\",\"UNAT\":\"Unattanium\",\"UNB\":\"UnbreakableCoin\",\"UNF\":\"Unfed-Coin\",\"UBT\":\"UniBright\",\"CANDY\":\"UnicornGo-Candy\",\"USX\":\"Unified-Society-USDEX\",\"UNIFY\":\"Unify\",\"UKG\":\"UnikoinGold\",\"UNIQ\":\"Uniqredit\",\"USDE\":\"UnitaryStatus-Dollar\",\"UAEC\":\"United-Arab-Emirates-Coin\",\"UTT\":\"United-Traders-Token\",\"UBTC\":\"UnitedBitcoin\",\"UIS\":\"Unitus\",\"UTN\":\"Universa\",\"UTNP\":\"Universa-Token\",\"UNIT\":\"Universal-Currency\",\"UNRC\":\"UniversalRoyalCoin\",\"UNI\":\"Universe\",\"UNO\":\"Unobtanium\",\"UP\":\"UpToken\",\"UFR\":\"Upfiring\",\"UQC\":\"Uquid-Coin\",\"URALS\":\"Urals-Coin\",\"URO\":\"UroCoin\",\"UETL\":\"Useless-Eth-Token-Lite\",\"UET\":\"Useless-Ethereum-Token\",\"UTH\":\"Uther\",\"UTIL\":\"Utility-Coin\",\"OOT\":\"Utrum\",\"UTK\":\"Utrust\",\"UWC\":\"Uwezocoin\",\"VIDT\":\"V-ID\",\"VEGA\":\"VEGA\",\"VIBE\":\"VIBEHub\",\"VIP\":\"VIP-Tokens\",\"VITE\":\"VITE\",\"VIVO\":\"VIVO-Coin\",\"VLUX\":\"VLUX\",\"VVI\":\"VV-Coin\",\"VLD\":\"Valid\",\"VAL\":\"Valorbit\",\"VLR\":\"Valorem\",\"VANY\":\"Vanywhere\",\"VPRC\":\"VapersCoin\",\"VAPOR\":\"Vaporcoin\",\"VLTC\":\"VaultCoin\",\"XVC\":\"Vcash\",\"VEN\":\"Vechain\",\"VEC2\":\"VectorCoin-2.0-\",\"VLX\":\"Velox\",\"VLT\":\"Veltor\",\"VRA\":\"Verasity\",\"VNT\":\"Veredictum\",\"XVG\":\"Verge\",\"VRC\":\"VeriCoin\",\"VME\":\"VeriME\",\"CRED\":\"Verify\",\"VERI\":\"Veritaseum\",\"VRM\":\"Verium\",\"VRS\":\"Veros\",\"VERSA\":\"Versa-Token\",\"VTC\":\"Vertcoin\",\"VTX\":\"Vertex\",\"VST\":\"Vestarin\",\"VZT\":\"Vezt\",\"VIA\":\"ViaCoin\",\"VIB\":\"Viberate\",\"VIT\":\"Vice-Industry-Token\",\"VTY\":\"Victoriouscoin\",\"VIC\":\"Victorium\",\"VID\":\"VideoCoin\",\"VDO\":\"VidioCoin\",\"VIEW\":\"Viewly\",\"VIN\":\"VinChain\",\"VIOR\":\"ViorCoin\",\"VIRAL\":\"Viral-Coin\",\"VUC\":\"Virta-Unique-Coin\",\"VTA\":\"VirtaCoin\",\"XVP\":\"VirtacoinPlus\",\"VMC\":\"VirtualMining-Coin\",\"VISIO\":\"Visio\",\"VITAE\":\"Vitae\",\"VIU\":\"Viuly\",\"VOISE\":\"Voise\",\"VTN\":\"Voltroon\",\"VOOT\":\"VootCoin\",\"VOT\":\"Votecoin\",\"VOYA\":\"Voyacoin\",\"VSX\":\"Vsync\",\"VTR\":\"Vtorrent\",\"VULC\":\"Vulcano\",\"W3C\":\"W3Coin\",\"WAB\":\"WABnetwork\",\"WIN\":\"WCoin\",\"WMC\":\"WMCoin\",\"WRT\":\"WRTcoin\",\"WABI\":\"WaBi\",\"WGR\":\"Wagerr\",\"WTC\":\"Waltonchain\",\"WAN\":\"Wanchain\",\"WAND\":\"WandX\",\"WRC*\":\"WarCoin\",\"WARP\":\"WarpCoin\",\"WASH\":\"WashingtonCoin\",\"WAVES\":\"Waves\",\"WCT\":\"Waves-Community-Token\",\"WGO\":\"WavesGO\",\"WNET\":\"Wavesnode.net\",\"WAY\":\"WayCoin\",\"WSX\":\"WeAreSatoshi\",\"WPR\":\"WePower\",\"WT\":\"WeToken\",\"WEALTH\":\"WealthCoin\",\"WEB*\":\"Webchain\",\"WELL\":\"Well\",\"WEX\":\"Wexcoin\",\"WHL\":\"WhaleCoin\",\"WC\":\"WhiteCoin\",\"XWC\":\"WhiteCoin\",\"WIC\":\"Wi-Coin\",\"WIIX\":\"Wiix\",\"WBB\":\"Wild-Beast-Coin\",\"WILD\":\"Wild-Crypto\",\"WINS\":\"WinStars\",\"LIF\":\"Winding-Tree\",\"WINE\":\"WineCoin\",\"WINGS\":\"Wings-DAO\",\"WINK\":\"Wink\",\"WISC\":\"WisdomCoin\",\"WSC\":\"WiserCoin\",\"WSH\":\"Wish-Finance\",\"WISH*\":\"WishFinance\",\"WLK\":\"Wolk\",\"WOMEN\":\"WomenCoin\",\"LOG\":\"Wood-Coin\",\"WCG\":\"World-Crypto-Gold\",\"WGC\":\"World-Gold-Coin\",\"XWT\":\"World-Trade-Funds\",\"WBTC\":\"WorldBTC\",\"WDC\":\"WorldCoin\",\"WOP\":\"WorldPay\",\"WRC\":\"Worldcore\",\"WAX\":\"Worldwide-Asset-eXchange\",\"WYR\":\"Wyrify\",\"WYS\":\"Wysker\",\"XRED\":\"X-Real-Estate-Development\",\"XC\":\"X11-Coin\",\"X2\":\"X2Coin\",\"X8X\":\"X8Currency\",\"XCO\":\"XCoin\",\"XDE2\":\"XDE-II\",\"XDNA\":\"XDNA\",\"XG\":\"XG-Sports\",\"XMX\":\"XMax\",\"XRP\":\"Ripple\",\"XUEZ\":\"XUEZ\",\"XXX\":\"XXXCoin\",\"XYO\":\"XY-Oracle\",\"XNX\":\"XanaxCoin\",\"XAU\":\"XauCoin\",\"XAUR\":\"Xaurum\",\"XCASH\":\"Xcash\",\"XCEL\":\"XcelTrip\",\"XNC\":\"XenCoin\",\"XEN\":\"XenixCoin\",\"XNN\":\"Xenon\",\"MI\":\"XiaoMiCoin\",\"XDCE\":\"XinFin-Coin\",\"XIOS\":\"Xios\",\"XT3\":\"Xt3ch\",\"XBY\":\"XtraBYtes\",\"YAY\":\"YAYcoin\",\"YAC\":\"YAcCoin\",\"YMC\":\"YamahaCoin\",\"YBC\":\"YbCoin\",\"YEE\":\"Yee\",\"YES\":\"YesCoin\",\"YOC\":\"YoCoin\",\"YOVI\":\"YobitVirtualCoin\",\"U42\":\"You42\",\"YOYOW\":\"Yoyow\",\"YUM\":\"Yumerium\",\"Z2\":\"Z2-Coin\",\"ZAB\":\"ZABERcoin\",\"ZCC\":\"ZCC-Coin\",\"ZEC\":\"ZCash\",\"ZECD\":\"ZCashDarkCoin\",\"ZCG\":\"ZCashGOLD\",\"ZCL\":\"ZClassic\",\"XZC\":\"ZCoin\",\"ZINC\":\"ZINC\",\"ZIX\":\"ZIX-Token\",\"ZLQ\":\"ZLiteQubit\",\"ZMN\":\"ZMINE\",\"ZSE\":\"ZSEcoin\",\"ZAP\":\"Zap\",\"ZYD\":\"ZayedCoin\",\"ZXT\":\"Zcrypt\",\"NZL\":\"Zealium\",\"ZCO\":\"Zebi-Coin\",\"ZED\":\"ZedCoins\",\"ZPT\":\"Zeepin\",\"ZEIT\":\"ZeitCoin\",\"ZEL\":\"Zelcash\",\"ZEN*\":\"Zen-Protocol\",\"ZEN\":\"ZenCash\",\"ZENI\":\"Zennies\",\"ZNA\":\"Zenome\",\"ZER\":\"Zero\",\"ZSC*\":\"ZeroState\",\"ZET2\":\"Zeta2Coin\",\"ZET\":\"ZetaCoin\",\"ZSC\":\"Zeusshield\",\"ZRC*\":\"ZiftrCoin\",\"ZBC\":\"Zilbercoin\",\"ZIL\":\"Zilliqa\",\"ZIPT\":\"Zippie\",\"ZOI\":\"Zoin\",\"ZNE\":\"ZoneCoin\",\"ZOOM\":\"ZoomCoin\",\"ZRC\":\"ZrCoin\",\"ZUP\":\"Zupply-Token\",\"ZUR\":\"Zurcoin\",\"AXP\":\"aXpire\",\"ELF\":\"aelf\",\"BITCNY\":\"bitCNY\",\"BITGOLD\":\"bitGold\",\"BITSILVER\":\"bitSilver\",\"BITUSD\":\"bitUSD\",\"DCS\":\"deCLOUDs\",\"DNT\":\"district0x\",\"ECHT\":\"e-Chat\",\"EBIT\":\"eBit\",\"EBTC\":\"eBitcoin\",\"EBCH\":\"eBitcoinCash\",\"EBST\":\"eBoost\",\"ELTC2\":\"eLTC\",\"DEM\":\"eMark\",\"ePRX\":\"eProxy\",\"EREAL\":\"eREAL\",\"EXMR\":\"eXMR-Monero\",\"EOSDAC\":\"eosDAC\",\"FDX\":\"fidentiaX\",\"GCN\":\"gCn-Coin\",\"IBANK\":\"iBankCoin\",\"DEAL\":\"iDealCash\",\"ICE\":\"iDice\",\"IETH\":\"iEthereum\",\"RLC\":\"iEx.ec\",\"ILT\":\"iOlite\",\"IW\":\"iWallet\",\"IXT\":\"iXledger\",\"ITM\":\"intimate.io\",\"ONG\":\"onG.social\",\"redBUX\":\"redBUX\",\"UGC\":\"ugChain\",\"VSL\":\"vSlice\",\"WBTC*\":\"wBTC\"}";
    static JSONObject names;

    public static String GetCoinName(String symbol) {
        try {
            if (names == null) {
                names = new JSONObject(mapString);
            }
            return names.getString(symbol);
        } catch (Exception e) {}
        return "Unknown-Coin";
    }
}

public class Balance extends Activity {
    //region STATIC VARS
    public static LinkedList<String> trackedIds = new LinkedList<>(); // sorted list of tracked symbols
    public static JSONObject data = new JSONObject(); // all app data for easy saving/loading

    static BalanceAdapter balanceAdapter = null;
    static Context actContext;

    static String SelectedCoin = ""; // symbol of the selected coin
    static boolean paused = false; // prevents updating when not active
    static boolean updating = false; // used to prevent concurrent updates
    static long updateDelay = 300000; // milliseconds between updates

    static boolean addToExistingBalance = false;

    static Map<String, String> currencies = new HashMap<>();
    //endregion

    //region DATA ACCESSORS
    public static String GetSortBy() {
        try {
            if (data.has("sortBy")) {
                return data.getString("sortBy");
            }
        } catch (Exception e) {}
        return "coin"; // sort by coin by default
    }

    public static void SetSortBy(String value) {
        try {
            data.put("sortBy", value);
        } catch (Exception e) {}
    }

    public static String GetCurrency() {
        try {
            if (data.has("currencyCode")) {
                return data.getString("currencyCode");
            }
        } catch (Exception e) {}
        return "USD"; // use USD by default
    }

    public static void SetCurrency(String value) {
        try {
            data.put("currencyCode", value);
        } catch (Exception e) {}
    }

    public static String GetCurrencySign() {
        if (currencies.containsKey(GetCurrency())) {
            return currencies.get(GetCurrency());
        }
        return "$";
    }

    public static boolean GetSortDesc() {
        try {
            if (data.has("sortDesc")) {
                return data.getBoolean("sortDesc");
            }
        } catch (Exception e) {}
        return true; // sort desc by default
    }

    public static void SetSortDesc(boolean value) {
        try {
            data.put("sortDesc", value);
        } catch (Exception e) {}
    }

    public static Date GetLastUpdate() {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return df.parse(data.getString("lastUpdate"));
        } catch (Exception e) {}
        return new Date(0); // use 1970 as default last update time
    }

    public static void SetLastUpdate() {
        try {
            Date now = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            data.put("lastUpdate", df.format(now));
        } catch (Exception e) {}
    }

    public static LinkedList<String> GetTrackedIds() {
        LinkedList<String> result = new LinkedList<>();
        try {
            for (int i = 0; i < data.getJSONObject("holdings").length(); i++){
                result.add(data.getJSONObject("holdings").names().getString(i));
            }
        } catch (Exception e) {}
        return result;
    }

    public static void RemoveTrackedId(String id) {
        try {
            data.getJSONObject("holdings").remove(id);
        } catch (Exception e) {}
    }

    public static String GetCoinName(String id) {
        try {
            return SymbolMap.GetCoinName(id);
        } catch (Exception e) {}
        return "ERROR";
    }

    public static Double GetExchangeRate() {
        try {
            return data.getDouble("ExchangeRate");
        } catch (Exception e) {}
        return 0d;
    }

    public static Double GetPrevExchangeRate() {
        try {
            return data.getDouble("prevExchangeRate");
        } catch (Exception e) {}
        return 0d;
    }

    public static void SetExchangeRate(Double rate) {
        try {
            data.put("ExchangeRate", rate);
        } catch (Exception e) {}
    }

    public static void SetPrevExchangeRate(Double rate) {
        try {
            data.put("prevExchangeRate", rate);
        } catch (Exception e) {}
    }

    public static Double GetCoinPrice(String id) {
        try {
            Double BTCValue = data.getJSONObject("values").getJSONObject(id).getDouble("BTCValue");
            return  BTCValue * GetExchangeRate();
        } catch (Exception e) {}
        return 0d;
    }

    public static Double GetCoinPrevPrice(String id) {
        try {
            Double BTCValue = data.getJSONObject("values").getJSONObject(id).getDouble("prevBTCValue");
            return  BTCValue * GetPrevExchangeRate();
        } catch (Exception e) {}
        return 0d;
    }

    public static Double GetCoinPriceChange(String id) {
        try {
            return 100 * (GetCoinPrice(id) - GetCoinPrevPrice(id)) / GetCoinPrevPrice(id);
        } catch (Exception e) {}
        return 0d;
    }

    public static Double GetCoinHoldings(String id) {
        try {
            return data.getJSONObject("holdings").getDouble(id);
        } catch (Exception e) {}
        return 0d;
    }

    public static void SetCoinHoldings(String id, double holdings) {
        try {
            // create tracked id list if it does not exist
            if (!data.has("holdings")) {
                data.put("holdings", new JSONObject());
            }

            // set holdings for id
            data.getJSONObject("holdings").put(id, holdings);
        } catch (Exception e) {}
    }

    public static Double GetCoinValue(String id) {
        return GetCoinPrice(id) * GetCoinHoldings(id);
    }

    public static Double GetCoinPrevValue(String id) {
        return GetCoinPrevPrice(id) * GetCoinHoldings(id);
    }
    //endregion

    //region SAVE/LOAD
    void Load() {
        try {
            // load data from file
            InputStream inputStream = openFileInput("saveData.json");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                data = new JSONObject(bufferedReader.readLine());
                inputStream.close();
            }
        }
        catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }
    }

    void Save() {
        try {
            // save data to file
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("saveData.json", Context.MODE_PRIVATE));
            outputStreamWriter.write(data.toString());
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("BalancesSave", e.toString());
        }
    }
    //endregion

    //region TITLE BUTTONS
    public void OpenReadme(View v) {
        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/KarmaPenny/CryptoWatch/blob/master/README.md"));
        startActivity(intent);
    }

    public void OpenCurrencyBox(View v) {
        // set the edit title and erase the balance input text
        ((TextView) findViewById(R.id.currencyInput)).setText("");

        // disable input
        ToggleInput(false);

        // show the edit box
        findViewById(R.id.setCurrencyBox).setVisibility(FrameLayout.VISIBLE);

        // show keyboard for balance input
        EditText editText = (EditText) findViewById(R.id.currencyInput);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void ChangeCurrency() {
        // get currency from input box
        String currency = ((TextView) findViewById(R.id.currencyInput)).getText().toString();

        // Close balance box
        CloseCurrencyBox();

        // update the currency
        SetCurrency(currency);

        // update listings
        Update();
    }

    public void CloseCurrencyBox() {
        // close keyboard
        EditText editText = (EditText) findViewById(R.id.currencyInput);
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        // hide the edit box
        findViewById(R.id.setCurrencyBox).setVisibility(FrameLayout.GONE);

        // enable input again
        ToggleInput(true);
    }

    public void Refresh(View v) {
        Update();
    }
    //endregion

    //region SORTING
    void Sort() {
        // populated tracked ids list
        trackedIds = GetTrackedIds();

        // create comparators for the sort
        Comparator comparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                int result = rhs.compareTo(lhs);
                if (GetSortDesc()) {
                    return result * -1;
                }
                return result;
            }
        };
        if (GetSortBy().equals("holdings")) {
            comparator = new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    int result = Double.compare(GetCoinValue(lhs), GetCoinValue(rhs));
                    if (GetSortDesc()) {
                        result *= -1;
                    }
                    return result;
                }
            };
        } else if (GetSortBy().equals("price")) {
            comparator = new Comparator<String>() {
                @Override
                public int compare(String lhs, String rhs) {
                    int result = Double.compare(GetCoinPriceChange(lhs), GetCoinPriceChange(rhs));
                    if (GetSortDesc()) {
                        result *= -1;
                    }
                    return result;
                }
            };
        }

        // sort the track ids list
        Collections.sort(trackedIds, comparator);
    }

    public void SortByValue(String value) {
        // if already sorting by this value
        if (GetSortBy().equals(value)) {
            // reverse the sort direction
            SetSortDesc(!GetSortDesc());
        }
        // if not already sorting by the value
        else {
            // sort desc by the value
            SetSortBy(value);
            SetSortDesc(true);
        }

        // update the display
        UpdateDisplay();
    }

    public void SortByCoin(View v) {
        SortByValue("coin");
    }

    public void SortByHoldings(View v) {
        SortByValue("holdings");
    }

    public void SortByPrice(View v) {
        SortByValue("price");
    }
    //endregion

    //region NEW COIN INPUT
    public void OpenCoinInputBox(View v) {
        // set the edit title and erase the balance input text
        ((TextView) findViewById(R.id.coinInput)).setText("");

        // disable input
        ToggleInput(false);

        // show the edit box
        findViewById(R.id.newCoinBox).setVisibility(FrameLayout.VISIBLE);

        // show keyboard for balance input
        EditText editText = (EditText) findViewById(R.id.coinInput);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void AddCoin() {
        // get symbol from coin input box
        String symbol = ((TextView) findViewById(R.id.coinInput)).getText().toString();

        // Close balance box
        CloseNewCoinBox();

        // Set selected coin to the mapped id
        SelectedCoin = symbol;

        // download the icon for this coin id
        DownloadIcon(SelectedCoin);

        // add to the existing balance
        addToExistingBalance = true;

        // open balance box
        OpenBalanceInputBox();
    }

    public void CloseNewCoinBox() {
        // close keyboard
        EditText editText = (EditText) findViewById(R.id.coinInput);
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        // hide the edit box
        findViewById(R.id.newCoinBox).setVisibility(FrameLayout.GONE);

        // enable input again
        ToggleInput(true);
    }
    //endregion

    //region BALANCE INPUT
    public void OpenBalanceInputBox() {
        // set the edit title and erase the balance input text
        if (addToExistingBalance) {
            ((TextView) findViewById(R.id.editTitle)).setText("Add to " + GetCoinName(SelectedCoin) + " Holdings");
            ((TextView) findViewById(R.id.balanceInput)).setText("");
        } else {
            ((TextView) findViewById(R.id.editTitle)).setText("Set " + GetCoinName(SelectedCoin) + " Holdings");
            ((TextView) findViewById(R.id.balanceInput)).setText(Double.toString(GetCoinHoldings(SelectedCoin)));
        }

        // disable input
        ToggleInput(false);

        // show the edit box
        findViewById(R.id.editBalanceBox).setVisibility(FrameLayout.VISIBLE);

        // show keyboard for balance input
        EditText editText = (EditText) findViewById(R.id.balanceInput);
        editText.requestFocus();
        editText.selectAll();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public void DeleteSelected(View v) {
        // delete the coin icon
        File file = new File(Balance.actContext.getApplicationInfo().dataDir + "/" + SelectedCoin + ".png");
        if (file.exists()) {
            file.delete();
        }

        // remove from tracked coins list
        RemoveTrackedId(SelectedCoin);

        // close the edit box
        CloseBalanceEditBox();

        // update the display
        UpdateDisplay();
    }

    public void SaveBalance() {
        // get holdings from input box
        String balance = ((TextView) findViewById(R.id.balanceInput)).getText().toString();

        // Close balance box
        CloseBalanceEditBox();

        // add the holdings input to the previous holdings
        double prevHoldings = GetCoinHoldings(SelectedCoin);
        double newHoldings = (balance.isEmpty()) ? 0 : Double.parseDouble(balance);

        // set holdings for selected coin
        if (addToExistingBalance) {
            SetCoinHoldings(SelectedCoin, prevHoldings + newHoldings);
        } else {
            SetCoinHoldings(SelectedCoin, newHoldings);
        }

        // update listings display
        UpdateDisplay();
    }

    public void CloseBalanceEditBox() {
        // close keyboard
        EditText editText = (EditText) findViewById(R.id.balanceInput);
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        // hide the edit box
        findViewById(R.id.editBalanceBox).setVisibility(FrameLayout.GONE);

        // enable input again
        ToggleInput(true);
    }
    //endregion

    public void LookupSelected() {
        String webSlug = GetCoinName(SelectedCoin).toLowerCase();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://coinmarketcap.com/currencies/" + webSlug));
        startActivity(intent);
    }

    public void ToggleInput(boolean toggle) {
        // toggle buttons on/off
        findViewById(R.id.balancesList).setEnabled(toggle);
        findViewById(R.id.refreshButton).setEnabled(toggle);
        findViewById(R.id.helpButton).setEnabled(toggle);
        findViewById(R.id.sortCoinButton).setEnabled(toggle);
        findViewById(R.id.sortHoldingsButton).setEnabled(toggle);
        findViewById(R.id.sortPriceButton).setEnabled(toggle);

        // toggle grey overlay on/off
        if (toggle) {
            findViewById(R.id.greyOverlay).setVisibility(LinearLayout.GONE);
        } else {
            findViewById(R.id.greyOverlay).setVisibility(LinearLayout.VISIBLE);
        }
    }

    void DownloadIcon(final String id) {
        Log.d("DOWNLOAD ICON", "id = " + id);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // download the icon if we do not already have a copy
                    File file = new File(Balance.actContext.getApplicationInfo().dataDir + "/" + id + ".png");
                    if (!file.exists()) {
                        String name = GetCoinName(id).toLowerCase();
                        URL iconUrl = new URL("https://raw.githubusercontent.com/ErikThiart/cryptocurrency-icons/master/128/" + name + ".png");
                        InputStream in = new BufferedInputStream(iconUrl.openStream());
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        byte[] buf = new byte[1024];
                        int n = 0;
                        while (-1 != (n = in.read(buf))) {
                            out.write(buf, 0, n);
                        }
                        out.close();
                        in.close();
                        byte[] response = out.toByteArray();

                        FileOutputStream fos = new FileOutputStream(Balance.actContext.getApplicationInfo().dataDir + "/" + id + ".png");
                        fos.write(response);
                        fos.close();
                    }
                } catch (Exception e) {
                    Log.e("Failed to download icon", e.getMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void empty) {
                super.onPostExecute(empty);
                UpdateDisplay();
            }
        }.execute();
    }

    //region UPDATE
    Handler handler = new Handler();
    void AutoUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!paused) {
                    Update();
                }
                AutoUpdate();
            }
        }, updateDelay);
    }

    boolean ShouldUpdate() {
        Date last_update = GetLastUpdate();
        Date next_update = new Date(last_update.getTime() + updateDelay);
        Date now = Calendar.getInstance().getTime();
        if (now.after(next_update)) {
            return true;
        }
        return false;
    }

    void Update() {
        if (!updating) {
            // prevent concurrent updates
            updating = true;

            // animate refresh button
            RotateAnimation anim = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(1000);
            final ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
            refreshButton.startAnimation(anim);

            // record the time of this update so we know when to update next
            SetLastUpdate();

            // perform update in the background
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        // get exchange rate from coinbase.com
                        URL url = new URL("https://api.coinbase.com/v2/prices/BTC-" + GetCurrency() + "/spot");
                        InputStream inputStream = url.openStream();
                        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder jsonBuilder = new StringBuilder("");
                        String line;
                        while ((line = in.readLine()) != null) {
                            jsonBuilder.append(line);
                        }
                        JSONObject result = new JSONObject(jsonBuilder.toString());
                        in.close();
                        SetExchangeRate(result.getJSONObject("data").getDouble("amount"));

                        // get yesterdays exchange rate from coinbase.com
                        Date now = Calendar.getInstance().getTime();
                        Date yesterdayDate = new Date(now.getTime() - 86400000);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String yesterday = df.format(yesterdayDate);
                        url = new URL("https://api.coinbase.com/v2/prices/BTC-" + GetCurrency() + "/spot?date=" + yesterday);
                        inputStream = url.openStream();
                        in = new BufferedReader(new InputStreamReader(inputStream));
                        jsonBuilder = new StringBuilder("");
                        while ((line = in.readLine()) != null) {
                            jsonBuilder.append(line);
                        }
                        result = new JSONObject(jsonBuilder.toString());
                        in.close();
                        SetPrevExchangeRate(result.getJSONObject("data").getDouble("amount"));

                        // get coin values from binance.com
                        url = new URL("https://api.binance.com/api/v1/ticker/24hr");
                        inputStream = url.openStream();
                        in = new BufferedReader(new InputStreamReader(inputStream));
                        jsonBuilder = new StringBuilder("{\"data\":");
                        while ((line = in.readLine()) != null) {
                            jsonBuilder.append(line);
                        }
                        jsonBuilder.append("}");
                        result = new JSONObject(jsonBuilder.toString());
                        in.close();
                        JSONObject values = new JSONObject();
                        JSONObject BTCValue = new JSONObject();
                        BTCValue.put("BTCValue", 1.0);
                        BTCValue.put("prevBTCValue", 1.0);
                        values.put("BTC", BTCValue);
                        JSONArray exchangeInfo = result.getJSONArray("data");
                        for (int i = 0; i < exchangeInfo.length(); i++) {
                            JSONObject coinInfo = exchangeInfo.getJSONObject(i);
                            String symbol = coinInfo.getString("symbol");
                            if (symbol.endsWith("BTC")) {
                                symbol = symbol.substring(0, symbol.length() - 3);
                                JSONObject coinValue = new JSONObject();
                                coinValue.put("BTCValue", coinInfo.getDouble("lastPrice"));
                                coinValue.put("prevBTCValue", coinInfo.getDouble("prevClosePrice"));
                                values.put(symbol, coinValue);
                            }
                        }
                        data.put("values", values);
                    } catch (Exception e) {
                        Log.e("UpdatePrices", e.toString());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void empty) {
                    super.onPostExecute(empty);
                    UpdateDisplay();
                }
            }.execute();
        }
    }

    void UpdateDisplay() {
        // save data
        Save();

        // build a sorted list of tracked symbols for displaying
        Sort();

        // update sort arrows
        TextView coinHeader = (TextView) findViewById(R.id.sortCoinButton);
        TextView holdingsHeader = (TextView) findViewById(R.id.sortHoldingsButton);
        TextView priceHeader = (TextView) findViewById(R.id.sortPriceButton);
        coinHeader.setText("Coin");
        holdingsHeader.setText("Holdings");
        priceHeader.setText("Price");
        String arrow = (GetSortDesc()) ? "↓" : "↑";
        if (GetSortBy().equals("coin")) {
            coinHeader.setText("Coin" + arrow);
        } else if (GetSortBy().equals("holdings")) {
            holdingsHeader.setText("Holdings" + arrow);
        } else if (GetSortBy().equals("price")) {
            priceHeader.setText("Price" + arrow);
        }

        // Update total value and previous value (used for percent change)
        double totalValue = 0;
        double previousValue = 0;
        for (int i = 0; i < trackedIds.size(); i++) {
            String id = trackedIds.get(i);
            totalValue += GetCoinValue(id);
            previousValue += GetCoinPrevValue(id);
        }
        ((TextView) findViewById(R.id.valueTotal)).setText(String.format("%1$,.2f", totalValue));

        // Update portfolio title
        String portfolioTitle = "Total Portfolio Value (" + GetCurrency() + ")";
        ((TextView) findViewById(R.id.portfolioValueTitle)).setText(portfolioTitle);
        ((TextView) findViewById(R.id.smallDollarSign)).setText(GetCurrencySign());

        // Update change value
        TextView changeText = (TextView) findViewById(R.id.change);
        double percentChange;
        if (previousValue == 0) {
            if (totalValue == 0) {
                percentChange = 0;
            } else {
                percentChange = 100;
            }
        } else {
            percentChange = 100 * (totalValue - previousValue) / previousValue;
        }
        if (percentChange > 0) {
            changeText.setText("+" + String.format("%1$,.2f", percentChange) + "%");
            changeText.setTextColor(Color.rgb(0, 150, 0));
        } else if (percentChange < 0) {
            changeText.setText(String.format("%1$,.2f", percentChange) + "%");
            changeText.setTextColor(Color.RED);
        } else {
            changeText.setText("+" + String.format("%1$,.2f", percentChange) + "%");
            changeText.setTextColor(Color.BLACK);
        }

        // set portfolio change arrow
        ImageView portfolioArrow = (ImageView) findViewById(R.id.portfolioChangeArrow);
        if (percentChange < 0) {
            portfolioArrow.setImageResource(R.drawable.arrow_red);
        } else if (percentChange > 0) {
            portfolioArrow.setImageResource(R.drawable.arrow_green);
        } else {
            portfolioArrow.setImageResource(R.drawable.dash);
        }

        // update list views
        balanceAdapter.notifyDataSetChanged();

        // stop animating refresh button
        final ImageView refreshButton = (ImageView) findViewById(R.id.refreshButton);
        refreshButton.setAnimation(null);

        // release lock on updating
        updating = false;
    }
    //endregion

    //region ACTIVITY METHODS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actContext = this;
        setContentView(R.layout.activity_balance);

        // initialize currencies mappings
        currencies.put("USD", "$");
        currencies.put("AUD", "$");
        currencies.put("BRL", "R$");
        currencies.put("CAD", "$");
        currencies.put("CHF", "");
        currencies.put("CLP", "$");
        currencies.put("CNY", "¥");
        currencies.put("CZK", "Kč");
        currencies.put("DKK", "kr");
        currencies.put("EUR", "€");
        currencies.put("GBP", "£");
        currencies.put("HKD", "$");
        currencies.put("HUF", "Ft");
        currencies.put("IDR", "Rp");
        currencies.put("ILS", "₪");
        currencies.put("INR", "₹");
        currencies.put("JPY", "¥");
        currencies.put("KRW", "₩");
        currencies.put("MXN", "$");
        currencies.put("MYR", "RM");
        currencies.put("NOK", "kr");
        currencies.put("NZD", "$");
        currencies.put("PHP", "₱");
        currencies.put("PKR", "₨");
        currencies.put("PLN", "zł");
        currencies.put("RUB", "\u20BD");
        currencies.put("SEK", "kr");
        currencies.put("SGD", "$");
        currencies.put("THB", "฿");
        currencies.put("TRY", "₺");
        currencies.put("TWD", "$");
        currencies.put("ZAR", "R");

        // load data from file
        Load();

        // setup balances list
        ListView listView = (ListView) findViewById(R.id.balancesList);
        balanceAdapter = new BalanceAdapter(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                SelectedCoin = (String) balanceAdapter.getItem(position);
                LookupSelected();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, int position, long id) {
                SelectedCoin = (String) balanceAdapter.getItem(position);
                addToExistingBalance = false; // overwrite existing balance
                OpenBalanceInputBox();
                return true;
            }
        });

        listView.setAdapter(balanceAdapter);

        // setup currency box
        BalanceEditText currencyInput = (BalanceEditText) findViewById(R.id.currencyInput);

        // save balance and close input box when submit is pressed on keyboard
        currencyInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ChangeCurrency();
                    return true;
                }
                return false;
            }
        });

        currencyInput.setKeyImeChangeListener(new BalanceEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                    CloseCurrencyBox();
                }
            }
        });

        // setup edit box
        BalanceEditText balanceInput = (BalanceEditText) findViewById(R.id.balanceInput);

        // save balance and close input box when submit is pressed on keyboard
        balanceInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SaveBalance();
                    return true;
                }
                return false;
            }
        });

        balanceInput.setKeyImeChangeListener(new BalanceEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                    CloseBalanceEditBox();
                }
            }
        });

        // setup new coin box
        BalanceEditText newCoinInput = (BalanceEditText) findViewById(R.id.coinInput);

        // save balance and close input box when submit is pressed on keyboard
        newCoinInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    AddCoin();
                    return true;
                }
                return false;
            }
        });

        newCoinInput.setKeyImeChangeListener(new BalanceEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
                    CloseNewCoinBox();
                }
            }
        });

        // Auto Update
        AutoUpdate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateDisplay();
        if (ShouldUpdate()) {
            Update();
        }
        paused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }
    //endregion
}
