import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFileServlet extends HttpServlet
{

		public void doPost(HttpServletRequest request, HttpServletResponse response)
						throws ServletException, IOException
							{
										response.setContentType("text/html");
												PrintWriter out = response.getWriter();


														DiskFileItemFactory factory = new DiskFileItemFactory();


																String upload = this.getServletContext().getRealPath("/upload/");

																		String temp = System.getProperty("java.io.tmpdir");

																				factory.setSizeThreshold(1024 * 1024 * 5);

																						factory.setRepository(new File(temp));

																								ServletFileUpload servletFileUpload = new ServletFileUpload(factory);


																										try
																													{
																																	List<FileItem> list = servletFileUpload.parseRequest(request);

																																				for (FileItem item : list)
																																								{
																																													String name = item.getFieldName();
																																																	InputStream is = item.getInputStream();

																																																					if (name.contains("content"))
																																																										{
																																																																System.out.println(inputStream2String(is));
																																																																				} else if(name.contains("file"))
																																																																									{
																																																																															try
																																																																																					{
																																																																																												inputStream2File(is, upload + "\\" + item.getName());
																																																																																																	} catch (Exception e)
																																																																																				{
																																																																																											e.printStackTrace();
																																																																																																}
																																																																																								}
																																																																							}
																																							
																																							out.write("success");
																																									} catch (FileUploadException e)
																												{
																																e.printStackTrace();
																																			out.write("failure");
																																					}

																														out.flush();
																																out.close();
																																	}


			public static String inputStream2String(InputStream is) throws IOException
					{
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
										int i = -1;
												while ((i = is.read()) != -1)
															{
																			baos.write(i);
																					}
														return baos.toString();
															}


				public static void inputStream2File(InputStream is, String savePath)
								throws Exception
									{
												System.out.println("save path :" + savePath);
														File file = new File(savePath);
																InputStream inputSteam = is;
																		BufferedInputStream fis = new BufferedInputStream(inputSteam);
																				FileOutputStream fos = new FileOutputStream(file);
																						int f;
																								while ((f = fis.read()) != -1)
																											{
																															fos.write(f);
																																	}
																										fos.flush();
																												fos.close();
																														fis.close();
																																inputSteam.close();
																																		
																																	}

}
