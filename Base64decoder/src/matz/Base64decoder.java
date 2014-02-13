package matz;

/* twitterログのvalue内容をBase64デコードする。
 * だいたいこのクラスはdssXXディレクトリ一つに対し7-8時間、bzの解凍と同時に行うと12時間強かかる。
 * 	$ java -jar Base64Decoder.jar
 * でメインクラスが勝手に走る。バックグラウンドプロセスにして、かつログアウト後も実行させるにはnohupを使う。
 * 	$ nohup java <option> <jarfile> [<class>[ <args>]] > std.log 2> err.log &
 * 末尾の&がバックグラウンド指定子。これはshファイルに対して使っても構わない（shの子プロセス全てに継承される。多分）。
 * コマンドライン引数は1つだけ受けられて、ディレクトリ番号（dssXXのXX部分）を指定できる。
 * 解凍と同時に行うならshで書いて使う。/home/matsuzawa/以下に置いてある。
 * このクラスは処理（デコード）の終わった元ログを容量節約のために削除していく点に注意。
 * org.apache.commons.codecパッケージを使っているので、これをサーバにも導入するか、あるいはjarに含めてやる必要がある。
 * eclipseのエクスポートでjarに含めてやりたいがイマイチやり方がわからないので、実行可能jarファイルとしてエクスポートした時の構成をbuild.xmlに記録してある（こっちだとなぜか出来る）。
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class Base64decoder {

	public static String Base64toUTF8 (String input) throws UnsupportedEncodingException { //Stringとして読んだBase64文字列をUTF8にデコードしてStringで返す
		if (!input.isEmpty()) {
			byte[] inputByte = input.getBytes();
			byte[] outputByte = Base64.decodeBase64(inputByte);
			String output = new String(outputByte, "UTF-8");
			output = replace(output, Pattern.compile("[\r\n\f\t]+"), " "); //置換後文字列中の改行等をスペースに置換
			output = "\"" + replace(output, Pattern.compile("\\$"), "＄") + "\""; //前方参照制御文字（$）を全角に変換しエスケープ
			return output;
		} else {
			return "null";
		}
	}

	public static String Base64toSJIS (String input) throws UnsupportedEncodingException { //Stringとして読んだBase64文字列をSJISにデコードしてStringで返す
		if (!input.isEmpty()) {
			byte[] inputByte = input.getBytes();
			byte[] outputByte = Base64.decodeBase64(inputByte);
			String output = new String(outputByte, "SJIS");
			output = replace(output, Pattern.compile("[\r\n\f\t]+"), " "); //置換後文字列中の改行をスペースに置換
			output = "\"" + replace(output, Pattern.compile("\\$"), "＄") + "\""; //前方参照制御文字（$）を全角に変換しエスケープ
			return output;
		} else {
			return "null";
		}
	}
	
	public static String replace(String contents, Pattern pattern, String replaceText){
		//渡された文字列に対する検索エンジン
		Matcher matcher = pattern.matcher(contents);
		//検索・置換済み文字列を受け取るバッファ
		StringBuffer sb = new StringBuffer();

		while(matcher.find()){ //検索実行
			matcher.appendReplacement(sb, replaceText); //バッファに置換後文字列を追加
		}
		
		matcher.appendTail(sb); //バッファに残存文字列を追加
		return sb.toString();
	}
		
	public static FilenameFilter getRegexFileFilter(String regex){ //正規表現によるファイル名フィルターを返す
		final String regex_ = regex;
		return new FilenameFilter() {
			public boolean accept(File file, String name){
				boolean ret = name.matches(regex_);
				return ret;
			}
		};
	}
	
	public static void printLog(String logMessage) throws IOException { 
		System.out.println(logMessage);
	}
	
	public static void main(String[] args) {
		try {
			/* ディレクトリはdss11～dss14まであり、圧縮状態で約105GBずつ。解凍すると約7倍に膨れる。
			 * 全ディレクトリを処理するならこのdnumを11-14まで回す。ここでコマンドライン引数args[0]を受けても可。
			 * 解凍したログはデコードするとおよそ74%に縮む（Base64の性質）。
			 * なんにせよサーバのHDD容量が不安なのであまり一度に大胆な量やらせないこと。そのためにargsから処理するディレクトリ番号を受けて少しずつやる。
			 */
			int dnum = 11;
			if (args.length == 1) dnum = Integer.parseInt(args[0]);
			String dnumXX = String.format("%2d", dnum);
			
			String infroot = "/home/laboshare/data/hotlink/dss" + dnumXX + "/";
			String outfroot = "/home/laboshare/decoded/hotlink/dss" + dnumXX + "/";
			
			File infrootFile = new File(infroot);
			String[] infpathList = infrootFile.list(); //ディレクトリ内のファイル・ディレクトリの名前リスト（String）を取得
			//同じようなことをするとき、直接ファイルパスオブジェクトを取得するならlistFile()があるが、ここでは後でパス名をさらにいじるのでStringで取る
			//data側のディレクトリならdssXX以下には必ず何らかのディレクトリがあるはずなのでこのリストが空やnullになることはなく安全。
			
			for (String path : infpathList) {
				String infpath = infroot + path + "/";
				File infpathFile = new File(infpath);
				
				String[] fnameList = infpathFile.list(getRegexFileFilter("DSS" + dnumXX + "_00\\d\\d\\.log"));
				//filter付きlistメソッドは一致するファイルが見つからなかった場合（あるいはそもそもディレクトリが空だった場合も）、nullでなく空の配列を返してくれる。
				//したがってこの下のforは嬉しいことにぬるぽを返さないはず（ここの例外処理を忘れてバッチを走らせてしまったあとに改めて確認してみたところ運良くこうなっていた）。
				
				for (String fname : fnameList) {
					//ファイルパス
					File inf = new File(infpath + fname);
					printLog("Processing:\t" + inf.getPath());
					//指定したパスのファイルをFileInputStreamに読み込み、SJISとしてInputStreamReaderに読み込む。
					InputStreamReader isr = new InputStreamReader(new FileInputStream(inf));
					//行単位のバッファ
					BufferedReader br = new BufferedReader(isr);
					//検索パターン			
					Pattern pattern = Pattern.compile("(\\s*)(\\S+?=)(\\S*)(\t|$)");
							
					String oldline;
					String newline = "";
					
					//出力パス
					File outf = new File(outfroot + path + "/" + fname);
					if (!outf.getParentFile().isDirectory()) {
						outf.getParentFile().mkdirs();
						printLog("Create directory:\t" + outf.getParent());
					}
					//出力ストリーム
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outf), "UTF-8");
					//行単位のバッファ出力
					BufferedWriter bw = new BufferedWriter(osw);			
					
					while((oldline = br.readLine()) != null){ //行をファイル末尾まで繰り返し読み込む
						//行単位の正規表現検索エンジン
						Matcher matcher = pattern.matcher(oldline);
						//検索・置換済み文字列を受け取るバッファ
						StringBuffer sb = new StringBuffer();
		
						while(matcher.find()){ //検索実行
		
							String replaceText =  "$1$2" + Base64toUTF8(matcher.group(3)) + "$4"; //前方参照のうちBase64値部分のみをデコード
							matcher.appendReplacement(sb, replaceText); //バッファに置換後文字列を追加
							
						}
						
						matcher.appendTail(sb); //バッファに残存文字列を追加
						newline = sb.toString();
						//ファイル書き込み
						bw.write(newline);
						bw.newLine();
					}
					
					bw.close();
					osw.close();
					
					printLog("Completed file:\t" + outf.getPath());
					
					br.close();
					isr.close();
					
					if (inf.delete()) printLog("Deleted source:\t" + inf.getPath());
					else printLog("Failed to delete:\t" + inf.getPath());
				}
			}
			
			printLog("Done.");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}