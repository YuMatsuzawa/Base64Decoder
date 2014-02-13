package matz;

/* twitter���O��value���e��Base64�f�R�[�h����B
 * �����������̃N���X��dssXX�f�B���N�g����ɑ΂�7-8���ԁAbz�̉𓀂Ɠ����ɍs����12���ԋ�������B
 * 	$ java -jar Base64Decoder.jar
 * �Ń��C���N���X������ɑ���B�o�b�N�O���E���h�v���Z�X�ɂ��āA�����O�A�E�g������s������ɂ�nohup���g���B
 * 	$ nohup java <option> <jarfile> [<class>[ <args>]] > std.log 2> err.log &
 * ������&���o�b�N�O���E���h�w��q�B�����sh�t�@�C���ɑ΂��Ďg���Ă��\��Ȃ��ish�̎q�v���Z�X�S�ĂɌp�������B�����j�B
 * �R�}���h���C��������1�����󂯂��āA�f�B���N�g���ԍ��idssXX��XX�����j���w��ł���B
 * �𓀂Ɠ����ɍs���Ȃ�sh�ŏ����Ďg���B/home/matsuzawa/�ȉ��ɒu���Ă���B
 * ���̃N���X�͏����i�f�R�[�h�j�̏I����������O��e�ʐߖ�̂��߂ɍ폜���Ă����_�ɒ��ӁB
 * org.apache.commons.codec�p�b�P�[�W���g���Ă���̂ŁA������T�[�o�ɂ��������邩�A���邢��jar�Ɋ܂߂Ă��K�v������B
 * eclipse�̃G�N�X�|�[�g��jar�Ɋ܂߂Ă�肽�����C�}�C�`�������킩��Ȃ��̂ŁA���s�\jar�t�@�C���Ƃ��ăG�N�X�|�[�g�������̍\����build.xml�ɋL�^���Ă���i���������ƂȂ����o����j�B
 */

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class Base64decoder {

	public static String Base64toUTF8 (String input) throws UnsupportedEncodingException { //String�Ƃ��ēǂ�Base64�������UTF8�Ƀf�R�[�h����String�ŕԂ�
		if (!input.isEmpty()) {
			byte[] inputByte = input.getBytes();
			byte[] outputByte = Base64.decodeBase64(inputByte);
			String output = new String(outputByte, "UTF-8");
			output = replace(output, Pattern.compile("[\r\n\f\t]+"), " "); //�u���㕶���񒆂̉��s�����X�y�[�X�ɒu��
			output = "\"" + replace(output, Pattern.compile("\\$"), "��") + "\""; //�O���Q�Ɛ��䕶���i$�j��S�p�ɕϊ����G�X�P�[�v
			return output;
		} else {
			return "null";
		}
	}

	public static String Base64toSJIS (String input) throws UnsupportedEncodingException { //String�Ƃ��ēǂ�Base64�������SJIS�Ƀf�R�[�h����String�ŕԂ�
		if (!input.isEmpty()) {
			byte[] inputByte = input.getBytes();
			byte[] outputByte = Base64.decodeBase64(inputByte);
			String output = new String(outputByte, "SJIS");
			output = replace(output, Pattern.compile("[\r\n\f\t]+"), " "); //�u���㕶���񒆂̉��s���X�y�[�X�ɒu��
			output = "\"" + replace(output, Pattern.compile("\\$"), "��") + "\""; //�O���Q�Ɛ��䕶���i$�j��S�p�ɕϊ����G�X�P�[�v
			return output;
		} else {
			return "null";
		}
	}
	
	public static String replace(String contents, Pattern pattern, String replaceText){
		//�n���ꂽ������ɑ΂��錟���G���W��
		Matcher matcher = pattern.matcher(contents);
		//�����E�u���ςݕ�������󂯎��o�b�t�@
		StringBuffer sb = new StringBuffer();

		while(matcher.find()){ //�������s
			matcher.appendReplacement(sb, replaceText); //�o�b�t�@�ɒu���㕶�����ǉ�
		}
		
		matcher.appendTail(sb); //�o�b�t�@�Ɏc���������ǉ�
		return sb.toString();
	}
		
	public static FilenameFilter getRegexFileFilter(String regex){ //���K�\���ɂ��t�@�C�����t�B���^�[��Ԃ�
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
			/* �f�B���N�g����dss11�`dss14�܂ł���A���k��ԂŖ�105GB���B�𓀂���Ɩ�7�{�ɖc���B
			 * �S�f�B���N�g������������Ȃ炱��dnum��11-14�܂ŉ񂷁B�����ŃR�}���h���C������args[0]���󂯂Ă��B
			 * �𓀂������O�̓f�R�[�h����Ƃ��悻74%�ɏk�ށiBase64�̐����j�B
			 * �Ȃ�ɂ���T�[�o��HDD�e�ʂ��s���Ȃ̂ł��܂��x�ɑ�_�ȗʂ�点�Ȃ����ƁB���̂��߂�args���珈������f�B���N�g���ԍ����󂯂ď��������B
			 */
			int dnum = 11;
			if (args.length == 1) dnum = Integer.parseInt(args[0]);
			String dnumXX = String.format("%2d", dnum);
			
			String infroot = "/home/laboshare/data/hotlink/dss" + dnumXX + "/";
			String outfroot = "/home/laboshare/decoded/hotlink/dss" + dnumXX + "/";
			
			File infrootFile = new File(infroot);
			String[] infpathList = infrootFile.list(); //�f�B���N�g�����̃t�@�C���E�f�B���N�g���̖��O���X�g�iString�j���擾
			//�����悤�Ȃ��Ƃ�����Ƃ��A���ڃt�@�C���p�X�I�u�W�F�N�g���擾����Ȃ�listFile()�����邪�A�����ł͌�Ńp�X��������ɂ�����̂�String�Ŏ��
			//data���̃f�B���N�g���Ȃ�dssXX�ȉ��ɂ͕K�����炩�̃f�B���N�g��������͂��Ȃ̂ł��̃��X�g�����null�ɂȂ邱�Ƃ͂Ȃ����S�B
			
			for (String path : infpathList) {
				String infpath = infroot + path + "/";
				File infpathFile = new File(infpath);
				
				String[] fnameList = infpathFile.list(getRegexFileFilter("DSS" + dnumXX + "_00\\d\\d\\.log"));
				//filter�t��list���\�b�h�͈�v����t�@�C����������Ȃ������ꍇ�i���邢�͂��������f�B���N�g�����󂾂����ꍇ���j�Anull�łȂ���̔z���Ԃ��Ă����B
				//���������Ă��̉���for�͊��������Ƃɂʂ�ۂ�Ԃ��Ȃ��͂��i�����̗�O������Y��ăo�b�`�𑖂点�Ă��܂������Ƃɉ��߂Ċm�F���Ă݂��Ƃ���^�ǂ������Ȃ��Ă����j�B
				
				for (String fname : fnameList) {
					//�t�@�C���p�X
					File inf = new File(infpath + fname);
					printLog("Processing:\t" + inf.getPath());
					//�w�肵���p�X�̃t�@�C����FileInputStream�ɓǂݍ��݁ASJIS�Ƃ���InputStreamReader�ɓǂݍ��ށB
					InputStreamReader isr = new InputStreamReader(new FileInputStream(inf));
					//�s�P�ʂ̃o�b�t�@
					BufferedReader br = new BufferedReader(isr);
					//�����p�^�[��			
					Pattern pattern = Pattern.compile("(\\s*)(\\S+?=)(\\S*)(\t|$)");
							
					String oldline;
					String newline = "";
					
					//�o�̓p�X
					File outf = new File(outfroot + path + "/" + fname);
					if (!outf.getParentFile().isDirectory()) {
						outf.getParentFile().mkdirs();
						printLog("Create directory:\t" + outf.getParent());
					}
					//�o�̓X�g���[��
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(outf), "UTF-8");
					//�s�P�ʂ̃o�b�t�@�o��
					BufferedWriter bw = new BufferedWriter(osw);			
					
					while((oldline = br.readLine()) != null){ //�s���t�@�C�������܂ŌJ��Ԃ��ǂݍ���
						//�s�P�ʂ̐��K�\�������G���W��
						Matcher matcher = pattern.matcher(oldline);
						//�����E�u���ςݕ�������󂯎��o�b�t�@
						StringBuffer sb = new StringBuffer();
		
						while(matcher.find()){ //�������s
		
							String replaceText =  "$1$2" + Base64toUTF8(matcher.group(3)) + "$4"; //�O���Q�Ƃ̂���Base64�l�����݂̂��f�R�[�h
							matcher.appendReplacement(sb, replaceText); //�o�b�t�@�ɒu���㕶�����ǉ�
							
						}
						
						matcher.appendTail(sb); //�o�b�t�@�Ɏc���������ǉ�
						newline = sb.toString();
						//�t�@�C����������
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